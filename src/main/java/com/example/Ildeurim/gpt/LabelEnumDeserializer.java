package com.example.Ildeurim.gpt;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.lang.reflect.Method;

public class LabelEnumDeserializer<E extends Enum<E>> extends StdDeserializer<E> {
    private final Class<E> enumType;
    public LabelEnumDeserializer(Class<E> t) { super(t); this.enumType = t; }

    @Override
    public E deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        String v = p.getValueAsString();
        if (v == null) return null;

        // 1) 라벨(getLabel)과 정확히 일치
        for (E e : enumType.getEnumConstants()) {
            String label = getLabel(e);
            if (label != null && label.equals(v)) return e;
        }
        // 2) enum 이름과 일치 (대소문자 무시)
        String up = v.trim().toUpperCase();
        for (E e : enumType.getEnumConstants()) {
            if (e.name().equals(up)) return e;
        }
        throw new InvalidFormatException(p,
                "Unknown "+enumType.getSimpleName()+" for value '"+v+"'", v, enumType);
    }

    private String getLabel(E e) {
        try {
            Method m = enumType.getMethod("getLabel");
            Object val = m.invoke(e);
            return val == null ? null : val.toString();
        } catch (Exception ignore) { return null; }
    }
}
