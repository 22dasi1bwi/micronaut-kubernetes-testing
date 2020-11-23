package com.example

import io.micronaut.context.ApplicationContext
import io.micronaut.context.annotation.Value
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.kubernetes.client.v1.KubernetesClient
import io.micronaut.kubernetes.client.v1.Metadata
import io.micronaut.kubernetes.client.v1.configmaps.ConfigMap
import io.micronaut.runtime.context.scope.Refreshable
import io.micronaut.runtime.context.scope.refresh.RefreshEvent
import org.reactivestreams.Publisher
import java.time.Instant

private const val DEFAULT_NAMESPACE = "default"
private const val CONFIG_MAP_NAME = "wipe-event"
private const val APP_LABEL = "demo"
private const val TIMESTAMP_PROPERTY_NAME = "timestamp"

@Controller("/")
internal class DemoController(
        private val client: KubernetesClient,
        private val timestampService: TimestampService,
        private val context: ApplicationContext
) {

    @Post("configmap/replace")
    internal fun replaceConfigMap(): Publisher<ConfigMap> {
        context.publishEvent(RefreshEvent(mapOf(TIMESTAMP_PROPERTY_NAME to "1970-01-01")))
        return client.replaceConfigMap(DEFAULT_NAMESPACE, CONFIG_MAP_NAME, buildConfigMap())
    }

    @Post("configmap/create")
    internal fun createConfigMap(): Publisher<ConfigMap> {
        return client.createConfigMap(DEFAULT_NAMESPACE, buildConfigMap())
    }

    @Get("configmap/timestamp")
    internal fun getTimestamp(): Instant = timestampService.getLatest()
}

/** Although this works just fine, for the pod which publishes the [RefreshEvent], all other pods wouldn't get their
 * [TimestampService] bean refreshed. In order to make sure, that the updated timestamp value is injected,
 * [TimestampService] needs to be a [io.micronaut.runtime.http.scope.RequestScope] bean. Depending on the use-case
 * that could consume an unwanted amount of additional memory.
 */
@Refreshable(TIMESTAMP_PROPERTY_NAME)
internal class TimestampService(@Value("\${timestamp:1970-01-01}") private val timestamp: String) {

    fun getLatest(): Instant = Instant.parse(timestamp)
}

internal fun buildConfigMap() =
        ConfigMap().apply {
            this.metadata = buildMetaData()
            this.data = mapOf(TIMESTAMP_PROPERTY_NAME to Instant.now().toString())
        }

internal fun buildMetaData() =
        Metadata().apply {
            this.name = CONFIG_MAP_NAME
            this.namespace = DEFAULT_NAMESPACE
            this.labels = mapOf("app" to APP_LABEL)
        }
