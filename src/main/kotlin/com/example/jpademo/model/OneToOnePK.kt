package com.example.jpademo.model

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
data class ParentSecond(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val pName: String,

        @Column
        val pContent: String,

        @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        @PrimaryKeyJoinColumn
        val child: ChildSecond

)

@Entity
data class ChildSecond(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val cName: String,

        @Column
        val cContent: String

)

interface ParentSecondRepository : JpaRepository<ParentSecond, Int>