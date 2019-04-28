package com.example.jpademo.model

import org.springframework.data.jpa.repository.JpaRepository
import javax.persistence.*

@Entity
data class ParentFirst(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val pName: String,

        @Column
        val pContent: String,

        @OneToOne(cascade = [CascadeType.ALL], fetch = FetchType.LAZY)
        val child: ChildFirst

)

@Entity
data class ChildFirst(

        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Int,

        @Column
        val cName: String,

        @Column
        val cContent: String

)

interface ParentFirstRepository : JpaRepository<ParentFirst, Int>