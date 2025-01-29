package com.licoflix.core.domain.repository;

import com.licoflix.core.domain.model.film.FilmCategory;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.List;

public class FilmCategorySpecification {

    public static Specification<FilmCategory> containsTextInAttributes(String text) {
        if (text == null || text.isEmpty()) {
            return (root, query, criteriaBuilder) -> criteriaBuilder.conjunction();
        }
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();
            addGlobalSearchConditions(predicates, builder, root, text);
            addDateConditions(predicates, builder, root, text);

            assert query != null;
            query.orderBy(builder.desc(root.join("film").get("createdIn")));
            return builder.or(predicates.toArray(new Predicate[0]));
        };
    }

    private static void addGlobalSearchConditions(List<Predicate> predicates, CriteriaBuilder builder, Root<FilmCategory> root, String text) {
        String likePattern = "%" + text.toLowerCase() + "%";

        try {
            int yearValue = Integer.parseInt(text);
            predicates.add(builder.equal(root.get("year"), yearValue));
        } catch (NumberFormatException ignored) {
        }

        predicates.add(builder.like(builder.lower(root.get("film").get("title")), likePattern));
        predicates.add(builder.like(builder.lower(root.get("category").get("name")), likePattern));
        predicates.add(builder.like(builder.lower(root.get("film").get("cast")), likePattern));
        predicates.add(builder.like(builder.lower(root.get("film").get("directors")), likePattern));
        predicates.add(builder.like(builder.lower(root.get("film").get("producers")), likePattern));
    }

    public static <T> void addDateConditions(List<Predicate> predicates, CriteriaBuilder builder, Root<T> root, String text) {
        List<Predicate> datePredicates = new ArrayList<>();
        try {
            getPredicateForFullDate(text, builder, root, "dd/MM/yyyy", datePredicates);
            getPredicateForFullDate(text, builder, root, "MM/dd/yyyy", datePredicates);
        } catch (Exception ignored) {
        }

        try {
            int year = Integer.parseInt(text);
            LocalDateTime startOfYear = LocalDate.of(year, 1, 1).atStartOfDay();
            LocalDateTime endOfYear = LocalDate.of(year, 12, 31).atTime(23, 59, 59);

            Predicate createdInPredicate = builder.between(root.get("film").get("createdIn"), startOfYear, endOfYear);
            Predicate changedInPredicate = builder.or(
                    builder.isNull(root.get("film").get("changedIn")),
                    builder.between(root.get("film").get("changedIn"), startOfYear, endOfYear)
            );

            predicates.add(builder.and(createdInPredicate, changedInPredicate));
        } catch (NumberFormatException ignored) {
        }

        predicates.addAll(datePredicates);
    }

    public static <T> void getPredicateForFullDate(String text, CriteriaBuilder builder, Root<T> root, String pattern, List<Predicate> predicates) {
        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern(pattern);
            LocalDateTime fullDate = LocalDate.parse(text, formatter).atStartOfDay();

            Expression<LocalDateTime> truncatedCreatedIn = builder.function(
                    "DATE_TRUNC", LocalDateTime.class,
                    builder.literal("day"), root.get("film").get("createdIn")
            );
            Predicate createdInPredicate = builder.equal(truncatedCreatedIn, fullDate);

            Predicate changedInPredicate = builder.or(
                    builder.equal(truncatedCreatedIn, fullDate),
                    builder.isNull(root.get("film").get("changedIn"))
            );

            predicates.add(builder.and(createdInPredicate, changedInPredicate));
        } catch (DateTimeParseException ignored) {
        }
    }
}