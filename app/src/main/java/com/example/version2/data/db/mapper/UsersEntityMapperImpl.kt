package com.example.version2.data.db.mapper

import com.example.version2.data.db.entity.DbUser
import com.example.version2.domain.model.User

object UsersEntityMapperImpl : UserEntityMapper<DbUser> {
    override fun fromEntity(entity: DbUser): User {
        return User(
            entity.firstName,
            entity.lastName,
            entity.email,
            entity.bio,
            entity.dob,
            entity.gender,
            entity.mobileNumber,
            entity.image,
            entity.addressLine1,
            entity.addressLine2,
            entity.city,
            entity.pinCode,
            entity.password,
            entity.isOnBoarded
        )
    }


    override fun toEntity(model: User, userId: Int): DbUser {
        return DbUser(
            model.firstName,
            model.lastName,
            model.email,
            model.bio,
            model.dob,
            model.gender,
            model.mobileNumber,
            model.image,
            model.addressLine1,
            model.addressLine2,
            model.city,
            model.pinCode,
            model.password,
            model.isOnBoarded,
            userId
        )
    }

}