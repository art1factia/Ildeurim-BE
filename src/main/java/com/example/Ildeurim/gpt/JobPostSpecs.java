package com.example.Ildeurim.gpt;

// JobPostSpecs.java
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import com.example.Ildeurim.dto.jobpost.JobPostFilter;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import jakarta.persistence.criteria.*;
import java.util.*;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.commons.enums.jobpost.*;

// JobPostSpecs.java
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.util.*;
import com.example.Ildeurim.domain.JobPost;
import com.example.Ildeurim.commons.enums.jobpost.*;
import com.example.Ildeurim.commons.enums.worker.WorkPlace;
import java.util.stream.Collectors;

@Component
public class JobPostSpecs {

    // 기존 필터 스펙(그대로)
    public Specification<JobPost> fromFilter(JobPostFilter f) {
        return jobFieldIn(f.jobField)
                .and(paymentTypeIn(f.paymentType))
                .and(paymentBetween(f.paymentMin, f.paymentMax))
                .and(workTypeEq(f.workType))
                .and(workDaysContainsAny(f.workDays))
                .and(workDaysCountBetween(f.workDaysCountMin, f.workDaysCountMax))
                .and(employmentTypeIn(f.employmentType))
                .and(applyMethodsIn(f.applyMethods))
                .and(locationLikeAny(f.locationContains))
                .and(statusEq(JobPostStatus.OPEN));
    }

    // ★ workPlace IN (선호값들) – 비어 있으면 “항상 false”가 되게 해서 matchRank=1로 떨어지게 함
    public Specification<JobPost> workPlaceInForMatch(Set<WorkPlace> s) {
        return (root, query, cb) -> {
            if (s == null || s.isEmpty()) {
                return cb.disjunction(); // 항상 false → soft-match 없음
            }
            return root.get("workPlace").in(s);
        };
    }

    // ★ 정렬 스펙: match 먼저 → workPlace 우선 → tailSort (id DESC 등)
//   주의: 여기서는 WHERE를 건드리지 않고 ORDER BY만 세팅함
    public Specification<JobPost> rankByMatchThenWorkPlace(
            Specification<JobPost> matchSpec,
            Set<WorkPlace> preferredWorkPlaces
    ) {
        return (root, query, cb) -> {
            // 1) 매칭 랭크: 일치=0, 불일치=1
            jakarta.persistence.criteria.Predicate match =
                    (matchSpec == null) ? cb.conjunction()
                            : matchSpec.toPredicate(root, query, cb);
            jakarta.persistence.criteria.Expression<Integer> matchRank =
                    cb.<Integer>selectCase().when(match, 0).otherwise(1);

            // 2) workPlace 랭크: 선호 목록 순서대로 0,1,2..., 그 외 999
            jakarta.persistence.criteria.Expression<Integer> wpRank;
            if (preferredWorkPlaces != null && !preferredWorkPlaces.isEmpty()) {
                var col = root.get("workPlace");
                var c = cb.<Integer>selectCase();
                int i = 0;
                for (WorkPlace wp : preferredWorkPlaces) {
                    c = c.when(cb.equal(col, wp), Integer.valueOf(i++));
                }
                wpRank = c.otherwise(Integer.valueOf(999));
            } else {
                wpRank = cb.literal(Integer.valueOf(0)); // 선호 없으면 영향 없음
            }

            // 3) ORDER BY 조립 (distinct 금지: Postgres ORDER BY 제한 회피)
            java.util.List<jakarta.persistence.criteria.Order> orders = new java.util.ArrayList<>();
            orders.add(cb.asc(matchRank));   // soft-match(마포) 먼저
            orders.add(cb.asc(wpRank));      // 선호 workPlace 순서
            query.orderBy(orders);

            return cb.conjunction(); // WHERE는 건드리지 않음
        };
    }

    // ===== 아래는 기존 보조 스펙들 =====
    public Specification<JobPost> statusEq(JobPostStatus s) {
        return (r,q,cb) -> (s==null)? cb.conjunction() : cb.equal(r.get("status"), s);
    }
    private Specification<JobPost> jobFieldIn(Set<JobField> s) {
        return (r,q,cb) -> (s==null || s.isEmpty())? cb.conjunction() : r.get("jobField").in(s);
    }
    private Specification<JobPost> paymentTypeIn(Set<PaymentType> s) {
        return (r,q,cb) -> (s==null || s.isEmpty())? cb.conjunction() : r.get("paymentType").in(s);
    }
    private Specification<JobPost> paymentBetween(Long min, Long max) {
        return (r,q,cb) -> {
            if (min==null && max==null) return cb.conjunction();
            Path<Long> p = r.get("payment");
            if (min!=null && max!=null) return cb.between(p, min, max);
            if (min!=null) return cb.greaterThanOrEqualTo(p, min);
            return cb.lessThanOrEqualTo(p, max);
        };
    }
    private Specification<JobPost> workTypeEq(WorkType t) {
        return (r,q,cb) -> (t==null)? cb.conjunction() : cb.equal(r.get("workType"), t);
    }
    // workDays ANY → EXISTS 서브쿼리
    private Specification<JobPost> workDaysContainsAny(Set<WorkDays> days) {
        return (root, query, cb) -> {
            if (days == null || days.isEmpty()) return cb.conjunction();

            Subquery<Long> sq = query.subquery(Long.class);
            Root<JobPost> jp = sq.from(JobPost.class);
            SetJoin<JobPost, WorkDays> jd = jp.joinSet("workDays", JoinType.INNER);

            sq.select(jp.get("id"))
                    .where(
                            cb.equal(jp.get("id"), root.get("id")),
                            jd.in(days)
                    );

            return cb.exists(sq);
        };
    }

    private Specification<JobPost> workDaysCountBetween(Integer min, Integer max) {
        return (r,q,cb) -> {
            if (min==null && max==null) return cb.conjunction();
            Path<Integer> p = r.get("workDaysCount");
            if (min!=null && max!=null) return cb.between(p, min, max);
            if (min!=null) return cb.greaterThanOrEqualTo(p, min);
            return cb.lessThanOrEqualTo(p, max);
        };
    }
    private Specification<JobPost> employmentTypeIn(Set<EmploymentType> s) {
        return (r,q,cb) -> (s==null || s.isEmpty())? cb.conjunction() : r.get("employmentType").in(s);
    }
    private Specification<JobPost> applyMethodsIn(Set<ApplyMethod> s) {
        return (root, query, cb) -> {
            if (s == null || s.isEmpty()) return cb.conjunction();

            // 서브쿼리 생성
            Subquery<Long> sq = query.subquery(Long.class);
            Root<JobPost> jp = sq.from(JobPost.class);

            // ElementCollection<ApplyMethod> 조인은 joinSet가 가장 명확
            SetJoin<JobPost, ApplyMethod> jm = jp.joinSet("applyMethods", JoinType.INNER);

            // 현재 row(root)와 동일한 JobPost에서, applyMethods 중 하나가 s에 존재하면 참
            sq.select(jp.get("id"))
                    .where(
                            cb.equal(jp.get("id"), root.get("id")),
                            jm.in(s)
                    );

            return cb.exists(sq);
        };
    }

    private Specification<JobPost> locationLikeAny(Set<String> words) {
        return (r,q,cb) -> {
            if (words==null || words.isEmpty()) return cb.conjunction();
            List<Predicate> ps = new ArrayList<>();
            for (String w : words) {
                ps.add(cb.like(cb.lower(r.get("location")), "%" + w.toLowerCase() + "%"));
            }
            return cb.or(ps.toArray(new Predicate[0]));
        };
    }
}
