/*
 * This file was copied from the Groovy source release:
 * org.codehaus.groovy.benchmarks.BuilderPerfTest
 * Modifications: parameterized testMe()
 */
package de.tud.stg.tests.instrumentation.benchmark.groovy

import groovy.xml.MarkupBuilder

class BuilderPerfTest{
    void formatAsXml(Writer writer) {
        def builder = new MarkupBuilder(writer)

        builder.Bookings {
            Booking {
                Origin("Auckland")
                Destination("Wellington")
                PassengerName("Mr John Smith")
            }
            Payment {
                From("J Smith")
                Amount(42)
            }
        }
    }

    void testMe (int size) {
        long start = System.currentTimeMillis()
        def writer
        size.times({
            writer = new StringWriter()
            formatAsXml(writer)
        })
        println "Took ${System.currentTimeMillis() - start} millis"
    }
}
