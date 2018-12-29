package com.house.demo.classes;

import javax.persistence.*;

@Entity
public class Application {

    @Id
    @GeneratedValue(strategy=GenerationType.AUTO)
    private Long id;

    public Integer year;

    public Integer eisodima;

    public Integer aderfia_stin_idia_poli;

    public Integer aderfia_se_diaforetiki_poli;

    public Integer status;

    public Long getId() {
        return id;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @OneToOne
    @JoinColumn(name="user_id")
    private User user;

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Integer getScore() {
        if (year > 4) {
            return -1;
        }
        if (status != 1) {
            return -1;
        }
        Integer score = 0;
        if (eisodima == 0) {
            score = 1000;
        }
        else if (eisodima < 10000) {
            score += 100;
        }
        else if (eisodima < 15000) {
            score += 30;
        }

        score += aderfia_stin_idia_poli*20;
        score += aderfia_se_diaforetiki_poli*50;
        return score;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getEisodima() {
        return eisodima;
    }

    public void setEisodima(Integer eisodima) {
        this.eisodima = eisodima;
    }

    public Integer getAderfia_stin_idia_poli() {
        return aderfia_stin_idia_poli;
    }

    public void setAderfia_stin_idia_poli(Integer aderfia_stin_idia_poli) {
        this.aderfia_stin_idia_poli = aderfia_stin_idia_poli;
    }

    public Integer getAderfia_se_diaforetiki_poli() {
        return aderfia_se_diaforetiki_poli;
    }

    public void setAderfia_se_diaforetiki_poli(Integer aderfia_se_diaforetiki_poli) {
        this.aderfia_se_diaforetiki_poli = aderfia_se_diaforetiki_poli;
    }
}