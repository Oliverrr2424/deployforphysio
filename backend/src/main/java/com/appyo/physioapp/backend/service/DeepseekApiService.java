package com.appyo.physioapp.backend.service;

import com.appyo.physioapp.backend.model.ChatCompletionRequest;
import com.appyo.physioapp.backend.model.ChatCompletionResponse;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface DeepseekApiService {
    @POST("chat/completions")
    Call<ChatCompletionResponse> createChatCompletion(@Body ChatCompletionRequest request);
} 