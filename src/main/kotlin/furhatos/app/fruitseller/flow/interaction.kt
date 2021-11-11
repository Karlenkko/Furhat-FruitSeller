package furhatos.app.fruitseller.flow

import furhatos.app.fruitseller.nlu.*
import furhatos.nlu.common.*
import furhatos.flow.kotlin.*
import furhatos.util.Language

val Start : State = state(Interaction) {

    onEntry {
        random(
                {furhat.say("Hi there")},
                {furhat.say("Oh, hey there")},
                {furhat.say("Welcome")}
        )
        goto(TakingOrder)
    }
}

val Options = state(Interaction) {
    onResponse<BuyFruit> {
        val fruits = it.intent.fruits
        if (fruits != null) {
            goto(OrderReceived(fruits))
        }
        else {
            propagate()
        }
    }

    onResponse<RequestOptions> {
        furhat.say("We have ${Fruit().getEnum(Language.ENGLISH_US).joinToString(", ")}")
        furhat.ask("Do you want some?")
    }

    onResponse<Yes> {
        random(
                { furhat.ask("What kind of fruit do you want?") },
                { furhat.ask("What type of fruit?") }
        )
    }
}


val TakingOrder = state(parent = Options) {
    onEntry {
        random(
                { furhat.ask("How about some fruits?") },
                { furhat.ask("Do you want some fruits?") }
        )
    }

    onResponse<No> {
        furhat.say("Okay, that's a shame. Have a splendid day!")
        goto(Idle)
    }
}

fun OrderReceived(fruits: FruitList) : State = state(Options) {
    onEntry {
        furhat.say("${fruits.text}, what a lovely choice!")
        fruits.list.forEach {
            users.current.order.fruits.list.add(it)
            // TODO: increment existing fruit
//            run breaking@{
//                var ord = it
//                if (users.current.order.fruits.list.isEmpty()) {
//                    users.current.order.fruits.list.add(it)
//                } else {
//                    users.current.order.fruits.list.forEach {
//                        if (it.fruit == ord.fruit) {
//                            it.count = it.count.plus(ord.count)
//                            return@breaking
//                        } else {
//                            users.current.order.fruits.list.add(it)
//                            return@breaking
//                        }
//                    }
//                }
//            }

        }
        furhat.ask("Anything else?")
    }

    onReentry {
        furhat.ask("Did you want something else?")
    }

    onResponse<No> {
        goto(ConfirmOrder)
    }
}

val ConfirmOrder = state(parent = TakingOrder) {
    onEntry {
        furhat.say("Okay, here is your order of ${users.current.order.fruits}.")
        furhat.ask("That's all you want, right?")
    }

    onReentry {
        furhat.say("Okay, here is your order of ${users.current.order.fruits}.")
        furhat.ask("That's all you want, right?")
    }

    onResponse<Yes> {
        furhat.say("Here you are. Have a nice day!")
        goto(Idle)
    }

    onResponse<No> {
        furhat.say("Sorry for the mistake. We will clear your basket and start over.")
        users.current.order.fruits.list.clear()
        goto(TakingOrder)
    }
}
