package com.hyerijang.dailypay.expense.domain;

import com.hyerijang.dailypay.budget.domain.Category;
import com.hyerijang.dailypay.common.entity.BaseTimeEntity;
import com.hyerijang.dailypay.user.domain.User;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "expense")
public class Expense extends BaseTimeEntity {

    @Id
    @Column(name = "expense_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Category category;

    @Column(name = "amount")
    @PositiveOrZero
    private Long amount;

    @Column(name = "memo")
    @NotNull
    private String memo;

    @Column(name = "excludeFromTotal")
    @NotNull
    private Boolean excludeFromTotal = false;

    @Column(name = "expenseDate")
    @NotNull
    private LocalDateTime expenseDate;

    @Column(name = "deleted")
    @NotNull
    Boolean deleted = false;


    // === Setter === //
    public void setUser(User user) {
        this.user = user;
    }

    // === 빌더 === //
    @Builder
    public Expense(User user, Category category, Long amount, String memo, Boolean excludeFromTotal,
        LocalDateTime expenseDate) {
        this.user = user;
        this.category = category;
        this.amount = amount;
        this.memo = memo;
        this.excludeFromTotal = excludeFromTotal;
        this.expenseDate = expenseDate;
    }

    // === 비즈니스 메서드 ===//
    public void update(Category category, Long amount, String memo, Boolean excludeFromTotal,
        LocalDateTime expenseDate) {
        if (category != null) {
            this.category = category;
        }
        if (amount != null) {
            this.amount = amount;
        }
        if (memo != null) {
            this.memo = memo;
        }
        if (excludeFromTotal != null) {
            this.excludeFromTotal = excludeFromTotal;
        }
        if (expenseDate != null) {
            this.expenseDate = expenseDate;
        }
    }

    public void delete() {
        this.deleted = true;
    }

    public void excludeFromTotal() {
        this.excludeFromTotal = true;
    }
}