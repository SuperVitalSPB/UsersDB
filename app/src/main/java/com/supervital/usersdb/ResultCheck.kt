package com.supervital.usersdb

sealed class ResultCheck () {
    class ResultOk() : ResultCheck()
    class NameExists() : ResultCheck()
    class NameMustEnter() : ResultCheck()
    class BadAge() : ResultCheck()
}