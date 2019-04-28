package com.example.jpademo

import com.example.jpademo.model.ChildFourth
import com.example.jpademo.model.ParentFourth
import com.example.jpademo.model.ParentFourthRepository
import org.hamcrest.MatcherAssert.assertThat
import org.hamcrest.Matchers.`is`
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager
import org.springframework.test.context.junit4.SpringRunner
import java.util.*


@Suppress("NonAsciiCharacters")
@RunWith(SpringRunner::class)
@DataJpaTest
class ManyToManyTest {

    @Autowired
    private lateinit var parentFourthRepository: ParentFourthRepository

    @Autowired
    private lateinit var em: TestEntityManager

    @Test
    fun `parent를 저장하면 children도 저장된다`() {
        // arrange
        val child1 = ChildFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), children)
        parentFourthRepository.save(parent)

        // actual
        val actual = parentFourthRepository.findAll().last().children.size

        // assert
        assertThat(actual, `is`(children.size))
    }

    @Test
    fun `parent의 children의 개수를 줄이면 child가 삭제된다`() {
        val child1 = ChildFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), children)
        parentFourthRepository.save(parent)



    }

    @Test
    fun `parent를 수정하면 children도 수정된다`() {
        // arrange
        val child = ChildFourth(0, "", "")
        val parent = ParentFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), mutableListOf(child))
        parentFourthRepository.save(parent)

        val savedParent = parentFourthRepository.findAll().last()
        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingChildren = savedParent.children.map { it.copy(cName = cName, cContent = cContent) }.toMutableList()
        val updatingParent = savedParent.copy(children = updatingChildren)
        parentFourthRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentFourthRepository.findAll().last().children.last()

        // assert
        assertThat(actual.cName, `is`(cName))
        assertThat(actual.cContent, `is`(cContent))
    }

    @Test
    fun `parent를 삭제하면 children이 삭제된다`() {
        // arrange
        val child1 = ChildFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentFourth(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), children)
        parentFourthRepository.save(parent)

        parentFourthRepository.delete(parent)

        // actual
        @Suppress("SqlNoDataSourceInspection", "SqlResolve")
        val actual = em.entityManager.createNativeQuery("select * from child_third").resultList

        // assert
        Assert.assertTrue(actual.isEmpty())
    }


}