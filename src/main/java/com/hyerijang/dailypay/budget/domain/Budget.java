package com.hyerijang.dailypay.budget.domain;


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
import jakarta.persistence.UniqueConstraint;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import java.time.YearMonth;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "budget",
    uniqueConstraints = @UniqueConstraint(columnNames = {"user_id", "yyyy_mm", "category"})

)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Budget extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "budget_id")
    private Long id;

    @Column(name = "category")
    @Enumerated(EnumType.STRING)
    @NotNull
    private Category category;

    @Column(name = "amount")
    @PositiveOrZero
    private Long budgetAmount;

    @Column(name = "yyyy_mm")
    @NotNull
    private YearMonth yearMonth; //년월

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id")
    @NotNull
    private User user;

    @Builder
    public Budget(Category category, Long budgetAmount, YearMonth yearMonth,
        @NotNull User user) {
        this.category = category;
        this.budgetAmount = budgetAmount;
        this.yearMonth = yearMonth;
        this.user = user;
    }

    // == 연관관계 메서드 == //
    public void setUser(User user) {
        this.user = user;
    }

    // == 비즈니스 메서드 == //
    public void updateBudgetAmount(Long budgetAmount) {
        // 예산 금액만 변경 가능
        this.category = category;
        this.budgetAmount = budgetAmount;
    }
}

