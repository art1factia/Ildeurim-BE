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

@Component
public class JobPostSpecs {

    public Specification<JobPost> fromFilter(JobPostFilter f) {
        return jobFieldIn(f.jobField)
                .and(paymentTypeIn(f.paymentType))
                .and(paymentBetween(f.paymentMin, f.paymentMax))
                .and(workTypeEq(f.workType))
                .and(workDaysContainsAny(f.workDays))
                .and(workDaysCountBetween(f.workDaysCountMin, f.workDaysCountMax))
                .and(employmentTypeIn(f.employmentType))
                .and(applyMethodsIn(f.applyMethods))
                .and(locationLikeAny(f.locationContains));
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
    private Specification<JobPost> workDaysContainsAny(Set<WorkDays> days) {
        // @ElementCollection Set<WorkDays> workDays
        return (r,q,cb) -> {
            if (days==null || days.isEmpty()) return cb.conjunction();
            Join<Object,Object> jd = r.join("workDays", JoinType.LEFT);
            return jd.in(days);
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
        if (s==null || s.isEmpty()) return (r,q,cb) -> cb.conjunction();
        // @ElementCollection Set<ApplyMethod> applyMethods
        return (r,q,cb) -> r.join("applyMethods", JoinType.LEFT).in(s);
    }
    private Specification<JobPost> locationLikeAny(Set<String> words) {
        return (r,q,cb) -> {
            if (words==null || words.isEmpty()) return cb.conjunction();
            List<jakarta.persistence.criteria.Predicate> ps = new ArrayList<>();
            for (String w : words) {
                ps.add(cb.like(cb.lower(r.get("location")), "%" + w.toLowerCase() + "%"));
            }
            return cb.or(ps.toArray(new jakarta.persistence.criteria.Predicate[0]));
        };
    }
    private Specification<JobPost> workPlaceIn(Set<WorkPlace> s) {
        return (r,q,cb) -> (s==null || s.isEmpty()) ? cb.conjunction() : r.get("workPlace").in(s);
    }
}
