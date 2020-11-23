package com.example

import io.micronaut.context.annotation.Value
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Post
import io.micronaut.kubernetes.client.v1.KubernetesClient
import io.micronaut.kubernetes.client.v1.Metadata
import io.micronaut.kubernetes.client.v1.configmaps.ConfigMap
import io.micronaut.runtime.context.scope.Refreshable
import org.reactivestreams.Publisher
import java.time.Instant

private const val DEFAULT_NAMESPACE = "default"
private const val CONFIG_MAP_NAME = "wipe-event"
private const val APP_LABEL = "demo"
private const val TIMESTAMP_PROPERTY_NAME = "timestamp"

@Controller("/")
internal class DemoController(
        private val client: KubernetesClient,
        private val timestampService: TimestampService
) {

    @Post("configmap/replace")
    internal fun replaceConfigMap(): Publisher<ConfigMap> {
        return client.replaceConfigMap(DEFAULT_NAMESPACE, CONFIG_MAP_NAME, buildConfigMap())
    }

    @Post("configmap/create")
    internal fun createConfigMap(): Publisher<ConfigMap> {
        return client.createConfigMap(DEFAULT_NAMESPACE, buildConfigMap())
    }

    @Get("configmap/timestamp")
    internal fun getTimestamp(): Instant = timestampService.getLatest()
}

@Refreshable
internal class TimestampService(@Value("\${timestamp:1970-01-01T00:00.000Z}") private val timestamp: String) {

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
