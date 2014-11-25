package com.mechzombie.test.gpars

import static groovyx.gpars.actor.Actors.actor
import org.junit.Test
import static org.junit.Assert.assertEquals

/**
 * Created by David on 11/11/2014.
 */
class ActorsFrameworkTest {

    @Test
    void testActors() {

        def sent = 'lellarap si yvoorG'
        def response

        def decryptor = actor {
            loop {
                react { message ->
                    if (message instanceof String) reply message.reverse()
                    else stop()
                }
            }
        }
        def console = actor {
            decryptor.send sent
            react {
                response = it
                println 'Decrypted message: ' + it
                decryptor.send false
            }
        }
        [decryptor, console]*.join()
        assertEquals sent.reverse(), response
    }
}
