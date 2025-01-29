package com.licoflix.core.domain.repository;

import com.licoflix.core.domain.model.film.Film;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class FilmSpecification {
    public static Specification<Film> containsTextInAttributes(String text, String category) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (category != null && !category.isEmpty()) {
                predicates.add(builder.equal(root.get("categories").get("name"), category));
            }

            if (text != null && !text.isEmpty()) {
                List<Predicate> textPredicates = new ArrayList<>();
                addGlobalSearchConditions(textPredicates, builder, root, text);

                if (!textPredicates.isEmpty()) {
                    predicates.add(builder.or(textPredicates.toArray(new Predicate[0])));
                }
            }

            if (predicates.isEmpty()) {
                return builder.conjunction();
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addGlobalSearchConditions(List<Predicate> predicates, CriteriaBuilder builder, Root<Film> root, String text) {
        String likePattern = "%" + text.toLowerCase() + "%";
        try {
            Long yearValue = Long.parseLong(text);
            predicates.add(builder.equal(root.get("year"), yearValue));
        } catch (NumberFormatException ignored) {
        }
        predicates.add(builder.like(builder.lower(root.get("cast")), likePattern));
        predicates.add(builder.like(builder.lower(root.get("title")), likePattern));
        predicates.add(builder.like(builder.lower(root.get("directors")), likePattern));
        predicates.add(builder.like(builder.lower(root.get("producers")), likePattern));
    }
}
