package com.example.jpademo.model

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
data class ParentThird(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val pName: String,

        @Column
        val pContent: String,

        @OneToMany(cascade = [CascadeType.ALL], orphanRemoval = true, fetch = FetchType.LAZY)
        @JoinColumn(name = "p_id") // 설정 안하면 parent_id로 생성됨
        val children: MutableList<ChildThird>

)

@Entity
data class ChildThird(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val cName: String,

        @Column
        val cContent: String

)

interface ParentThirdRepository : JpaRepository<ParentThird, Int>