package org.springframework.samples.petclinic.owner

import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.test.context.junit4.SpringRunner
import org.springframework.transaction.annotation.Transactional


@RunWith(SpringRunner::class)
@DataJpaTest
class OwnerRepositoryTest {

    @Autowired
    lateinit private var owners: OwnerRepository

    @Test
    fun shouldFindOwnersByLastName() {
        var owners = this.owners.findByLastName("Davis")
        assertThat(owners.size).isEqualTo(2)

        owners = this.owners.findByLastName("Daviss")
        assertThat(owners.isEmpty()).isTrue()
    }

    @Test
    fun shouldFindSingleOwnerWithPet() {
        val owner = this.owners.findById(1)
        assertThat(owner.lastName).startsWith("Franklin")
        assertThat(owner.pets.size).isEqualTo(1)
        assertThat(owner.pets[0].type).isNotNull()
        assertThat(owner.pets[0].type!!.name).isEqualTo("cat")
    }

    @Test
    @Transactional
    fun shouldInsertOwner() {
        var owners = this.owners.findByLastName("Schultz")
        val found = owners.size

        val owner = Owner(firstName = "Sam",
                lastName = "Schultz",
                address = "4, Evans Street",
                city = "Wollongong",
                telephone = "4444444444")
        this.owners.save(owner)
        assertThat(owner.id?.toLong()).isNotEqualTo(0)

        owners = this.owners.findByLastName("Schultz")
        assertThat(owners.size).isEqualTo(found + 1)
    }

    @Test
    @Transactional
    fun shouldUpdateOwner() {
        var owner = this.owners.findById(1)
        val oldLastName = owner.lastName
        val newLastName = oldLastName + "X"

        owner = owner.copy(lastName = newLastName)
        this.owners.save(owner)

        // retrieving new name from database
        owner = this.owners.findById(1)
        assertThat(owner.lastName).isEqualTo(newLastName)
    }
}
