package furhatos.app.fruitseller.flow

import furhatos.flow.kotlin.*
import furhatos.records.User
import furhatos.util.*
import java.util.*

var lastUserList: Deque<User> = ArrayDeque<User>()
var lastUser: User = User()
var lastUserExist = false

val Idle: State = state {

    init {
        furhat.setVoice(Language.ENGLISH_US, Gender.MALE)
        if (users.count > 0) {
            furhat.attend(users.random)
            goto(Start)
        }
    }

    onEntry {
        if (users.count == 0) {
            furhat.attendNobody()
        } else {
            lastUserExist = false
            furhat.glance(users.random)
            furhat.attend(users.random)
            goto(Start)
        }
    }

    onUserEnter {
//        lastUserList.push(it)
//        lastUserList.forEach{
//            println(it.id)
//        }
//        println()
        lastUser = it
        furhat.attend(it)
        goto(Start)
    }
}

val Interaction: State = state {
    onUserLeave(instant = true) {
        if (users.count > 0) {
            if (it == users.current) {
                furhat.attend(users.other)
                goto(Start)
            } else {
                furhat.glance(it)
            }
        } else {
            goto(Idle)
        }
    }

    onUserEnter() {
        lastUserExist = true
        if (users.count > 0) {
            furhat.glance(it)
            furhat.attend(it)
            furhat.say("I will help you later")
            furhat.glance(lastUser)
            furhat.attend(lastUser)
            reentry()
        }
        furhat.glance(it)
    }
//    onUserEnter() {
//        lastUserList.push(it)
//        lastUserList.forEach{
//            println(it.id)
//        }
//        println()
//        if (lastUserList.size > 0) {
//            furhat.glance(it)
//            furhat.attend(it)
//            furhat.say("I will help you later")
//            furhat.glance(lastUserList.last)
//            furhat.attend(lastUserList.last)
//            reentry()
//        } else {
//            furhat.glance(it)
//        }
//
//    }
//    onUserLeave() {
//        if (lastUserList.size > 0) {
//            if (it == users.current) {
//                furhat.attend(lastUserList.last)
//                goto(Start)
//            } else {
//                furhat.glance(it)
//                lastUserList.remove(it)
//            }
//        } else {
//            goto(Idle)
//        }
//    }

}