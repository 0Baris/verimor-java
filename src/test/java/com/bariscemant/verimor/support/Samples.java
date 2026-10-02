package com.bariscemant.verimor.support;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/** Builds a value for any parameter or generated model type by reflection. */
public final class Samples {
    private Samples() {
    }

    public static Object create(Type type) {
        return create(type, 0);
    }

    private static Object create(Type type, int depth) {
        if (type instanceof ParameterizedType) {
            ParameterizedType parameterized = (ParameterizedType) type;
            Class<?> raw = (Class<?>) parameterized.getRawType();
            if (List.class.isAssignableFrom(raw)) {
                List<Object> list = new ArrayList<>();
                list.add(create(parameterized.getActualTypeArguments()[0], depth + 1));
                return list;
            }
            if (Map.class.isAssignableFrom(raw)) {
                return new HashMap<>();
            }
            return create(raw, depth);
        }
        Class<?> cls = (Class<?>) type;
        if (cls == String.class || cls == Object.class) return "x";
        if (cls == long.class || cls == Long.class) return 1L;
        if (cls == int.class || cls == Integer.class) return 1;
        if (cls == boolean.class || cls == Boolean.class) return true;
        if (cls == double.class || cls == Double.class) return 1.0d;
        if (cls == float.class || cls == Float.class) return 1.0f;
        if (cls == BigDecimal.class) return BigDecimal.ONE;
        if (cls == UUID.class) return UUID.fromString("3f2504e0-4f89-41d3-9a0c-0305e82c3301");
        if (cls == OffsetDateTime.class) return OffsetDateTime.parse("2026-01-01T00:00:00Z");
        if (cls == LocalDate.class) return LocalDate.parse("2026-01-01");
        if (cls.isEnum()) return cls.getEnumConstants()[0];
        try {
            Object instance = cls.getDeclaredConstructor().newInstance();
            if (depth < 4) {
                for (Method setter : cls.getMethods()) {
                    if (setter.getName().startsWith("set") && setter.getParameterCount() == 1
                            && !Modifier.isStatic(setter.getModifiers())) {
                        setter.invoke(instance, create(setter.getGenericParameterTypes()[0], depth + 1));
                    }
                }
            }
            return instance;
        } catch (ReflectiveOperationException exception) {
            throw new IllegalStateException("Cannot build a sample of " + cls, exception);
        }
    }
}
