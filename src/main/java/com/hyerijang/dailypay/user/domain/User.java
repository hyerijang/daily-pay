package com.hyerijang.dailypay.user.domain;

import com.hyerijang.dailypay.budget.domain.Budget;
import com.hyerijang.dailypay.common.entity.BaseTimeEntity;
import com.hyerijang.dailypay.expense.domain.Expense;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Table(name = "user")
@Getter
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class User extends BaseTimeEntity implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id", updatable = false)
    private Long id;

    @NotBlank
    @Email
    @Column(name = "email", unique = true)
    private String email;

    @NotBlank
    @Column(name = "password")
    private String password;

    @OneToMany(mappedBy = "user")
    private List<Budget> budgetList = new ArrayList<>();

    @OneToMany(mappedBy = "user")
    private List<Expense> expenseList = new ArrayList<>();
    //=== 연관관계 메서드 ===//

    private void addBudget(Budget budget) {
        budgetList.add(budget);
        budget.setUser(this);
    }

    private void addExpense(Expense expense) {
        expenseList.add(expense);
        expense.setUser(this);
    }

    // === UserDetails 오버라이딩 ===

    @Builder
    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return null; // 아직 인가 구현 안함
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
