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
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "expense")
public class Expense extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Category category;

    @Column(nullable = false)
    private Long amount;

    @Column(length = 100, nullable = false)
    private String memo;

    @Column(nullable = false)
    private boolean excludeFromTotal;

    @Column(nullable = false)
    private LocalDateTime expenseDate;

    // === 연관관계 메서드 ===//
    public void setUser(User user) {
        this.user = user;
    }
}