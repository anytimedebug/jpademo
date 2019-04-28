package com.example.jpademo

import com.example.jpademo.model.ChildThird
import com.example.jpademo.model.ParentThird
import com.example.jpademo.model.ParentThirdRepository
import org.hamcrest.Matchers.`is`
import org.hamcrest.MatcherAssert.assertThat
import org.junit.Assert.assertTrue
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
class OneToManyTest {

    @Autowired
    private lateinit var parentThirdRepository: ParentThirdRepository

    @Autowired
    private lateinit var em: TestEntityManager

    @Test
    fun `parent를 저장하면 children도 저장된다`() {
        // arrange
        val child1 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), children)
        parentThirdRepository.save(parent)

        // actual
        val actual = parentThirdRepository.findAll().last().children.size

        // assert
        assertThat(actual, `is`(children.size))
    }

    @Test
    fun `parent의 children의 개수를 줄이면 child가 삭제된다`() {
        // arrange
        val child1 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), children)
        parentThirdRepository.save(parent)

        val savedParent = parentThirdRepository.findAll().last()
        val updatingChildren = savedParent.children
                .filterIndexed { index, _ -> index < children.size - 1 }
                .toMutableList()
        val updatingParent = savedParent.copy(children = updatingChildren)
        parentThirdRepository.save(updatingParent)

        // actual
        @Suppress("SqlNoDataSourceInspection", "SqlResolve")
        val actual = em.entityManager.createNativeQuery("select * from child_third").resultList.size

        // assert
        assertThat(actual, `is`(updatingChildren.size))
    }

    @Test
    fun `parent의 children의 개수를 추가하면 child가 추가된다`() {
        // arrange
        val child1 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), children)
        parentThirdRepository.save(parent)

        val savedParent = parentThirdRepository.findAll().last()
        val child4 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val updatingParent = savedParent.copy()
        updatingParent.children.add(child4)
        parentThirdRepository.save(updatingParent)

        // actual
        @Suppress("SqlNoDataSourceInspection", "SqlResolve")
        val actual = em.entityManager.createNativeQuery("select * from child_third").resultList.size

        // assert
        assertThat(actual, `is`(updatingParent.children.size))
    }

    @Test
    fun `parent를 수정하면 children도 수정된다`() {
        // arrange
        val child = ChildThird(0, "", "")
        val parent = ParentThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), mutableListOf(child))
        parentThirdRepository.save(parent)

        val savedParent = parentThirdRepository.findAll().last()
        val cName = UUID.randomUUID().toString()
        val cContent = UUID.randomUUID().toString()
        val updatingChildren = savedParent.children.map { it.copy(cName = cName, cContent = cContent) }.toMutableList()
        val updatingParent = savedParent.copy(children = updatingChildren)
        parentThirdRepository.saveAndFlush(updatingParent)

        // actual
        val actual = parentThirdRepository.findAll().last().children.last()

        // assert
        assertThat(actual.cName, `is`(cName))
        assertThat(actual.cContent, `is`(cContent))
    }

    @Test
    fun `parent를 삭제하면 children이 삭제된다`() {
        // arrange
        val child1 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child2 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val child3 = ChildThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString())
        val children = mutableListOf(child1, child2, child3)
        val parent = ParentThird(0, UUID.randomUUID().toString(), UUID.randomUUID().toString(), children)
        parentThirdRepository.save(parent)

        parentThirdRepository.delete(parent)

        // actual
        @Suppress("SqlNoDataSourceInspection", "SqlResolve")
        val actual = em.entityManager.createNativeQuery("select * from child_third").resultList

        // assert
        assertTrue(actual.isEmpty())
    }


}