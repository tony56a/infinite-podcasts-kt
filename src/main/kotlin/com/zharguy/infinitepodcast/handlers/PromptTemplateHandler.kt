package com.zharguy.infinitepodcast.handlers

import build.buf.gen.com.zharguy.protos.scripts.v1.CreateTemplateRequest
import build.buf.gen.com.zharguy.protos.scripts.v1.CreateTemplateResponse
import build.buf.gen.com.zharguy.protos.scripts.v1.TemplateServiceGrpcKt
import build.buf.gen.com.zharguy.protos.scripts.v1.createTemplateResponse
import com.zharguy.infinitepodcast.services.PromptTemplateService
import com.zharguy.infinitepodcast.services.mappers.toPromptTemplateModel
import com.zharguy.infinitepodcast.services.mappers.toProto
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.inject.Inject
import jakarta.inject.Singleton

private val logger = KotlinLogging.logger {}

@Singleton
class PromptTemplateHandler : TemplateServiceGrpcKt.TemplateServiceCoroutineImplBase() {

    @Inject
    lateinit var promptTemplateService: PromptTemplateService

    override suspend fun createTemplate(request: CreateTemplateRequest): CreateTemplateResponse {
        val model = request.template.toPromptTemplateModel()
        logger.info("Handling createTemplate", *model.getLogArgs())

        val promptTemplateModel = promptTemplateService.createPromptTemplate(model)
        return createTemplateResponse {
            this.template = promptTemplateModel.toProto()
        }

    }
}