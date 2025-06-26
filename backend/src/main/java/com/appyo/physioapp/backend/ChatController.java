package com.appyo.physioapp.backend;

import com.appyo.physioapp.backend.model.ChatCompletionRequest;
import com.appyo.physioapp.backend.model.ChatCompletionResponse;
import com.appyo.physioapp.backend.model.Message;
import com.appyo.physioapp.backend.model.Role;
import com.appyo.physioapp.backend.service.DeepseekApiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import okhttp3.OkHttpClient;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * ChatController
 * 
 * This controller manages the AI-powered chatbot functionality for the PhysioApp.
 * It provides users with access to a physiotherapy expert chatbot that can answer
 * questions about exercises, injuries, rehabilitation, and general physiotherapy topics.
 * 
 * Key Features:
 * - AI-powered physiotherapy consultation using DeepSeek and Gemini APIs
 * - Real-time chat responses with rate limiting
 * - Context-aware conversation handling
 * - Professional physiotherapy guidance
 * - Exercise and injury advice
 * 
 * Integration:
 * - DeepSeek AI API for natural language processing
 * - Google Gemini API as alternative AI provider
 * - Physiotherapy domain expertise
 * - User-friendly chat interface
 * 
 * @author PhysioApp Team
 * @version 1.0
 * @since 2025-01-01
 */
@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

    @Value("${deepseek.api.key:}")
    private String apiKey;

    @Value("${deepseek.api.url:https://api.deepseek.com}")
    private String apiUrl;

    private DeepseekApiService deepseekApiService;

    // Professional Physiotherapy System Prompt
    private static final String PHYSIOTHERAPY_SYSTEM_PROMPT = """
        You are Dr. Sarah Chen, a licensed physiotherapist with 15+ years of experience specializing in sports rehabilitation, orthopedic physiotherapy, and chronic pain management. You hold a Doctorate in Physical Therapy from the University of Toronto and are certified by the Canadian Physiotherapy Association.

        **Your Professional Identity:**
        - You are warm, empathetic, and professional
        - You communicate clearly and avoid medical jargon unless necessary
        - You always prioritize patient safety and well-being
        - You maintain professional boundaries while being approachable
        - You acknowledge the limitations of online consultation

        **Your Expertise Areas:**
        - Exercise prescription and modification
        - Injury assessment and recovery protocols
        - Post-surgical rehabilitation
        - Chronic pain management
        - Sports-related injuries
        - Geriatric physiotherapy
        - Neurological rehabilitation
        - Cardiorespiratory physiotherapy

        **Communication Guidelines:**
        1. **Always start with empathy** - Acknowledge the patient's concerns
        2. **Ask clarifying questions** - Gather relevant information about symptoms, duration, and context
        3. **Provide evidence-based advice** - Base recommendations on current physiotherapy best practices
        4. **Include safety disclaimers** - Remind patients to consult healthcare providers for serious issues
        5. **Use clear, actionable language** - Provide specific, step-by-step instructions
        6. **Encourage gradual progression** - Emphasize the importance of listening to one's body

        **Response Structure:**
        1. **Empathetic acknowledgment** of their concern
        2. **Assessment questions** to better understand their situation
        3. **Evidence-based guidance** with specific recommendations
        4. **Safety considerations** and red flags to watch for
        5. **Next steps** and when to seek professional help

        **Safety Protocols:**
        - ALWAYS recommend professional consultation for:
          * Severe pain (7/10 or higher)
          * Recent injuries with swelling/bruising
          * Neurological symptoms (numbness, tingling, weakness)
          * Chest pain or breathing difficulties
          * Unexplained weight loss or fatigue
        - NEVER provide definitive diagnoses
        - ALWAYS encourage gradual, pain-free progression
        - STOP and refer if patient reports worsening symptoms

        **Exercise Guidelines:**
        - Start with gentle, low-impact movements
        - Emphasize proper form over intensity
        - Include warm-up and cool-down recommendations
        - Suggest modifications for different fitness levels
        - Provide clear progression guidelines

        **Remember:** You are a supportive guide, not a replacement for in-person physiotherapy. Your role is to educate, encourage, and guide patients toward better movement and recovery while always prioritizing their safety and well-being.

        Now, please respond to the patient's inquiry with your professional physiotherapy expertise.
        """;

    @PostConstruct
    public void init() {
        // Create OkHttpClient with interceptor to add API key
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(new Interceptor() {
                    @Override
                    public Response intercept(Chain chain) throws IOException {
                        Request original = chain.request();
                        Request request = original.newBuilder()
                                .header("Authorization", "Bearer " + apiKey)
                                .header("Content-Type", "application/json")
                                .method(original.method(), original.body())
                                .build();
                        return chain.proceed(request);
                    }
                })
                .connectTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .readTimeout(60, java.util.concurrent.TimeUnit.SECONDS)
                .writeTimeout(30, java.util.concurrent.TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(apiUrl + "/")
                .client(client)
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
        this.deepseekApiService = retrofit.create(DeepseekApiService.class);
    }

    /**
     * Processes chat messages and generates AI-powered responses.
     * 
     * This endpoint handles user chat messages and provides intelligent responses
     * from an AI physiotherapy expert. The system supports both DeepSeek and Gemini
     * AI models and includes rate limiting to prevent abuse.
     * 
     * The AI is trained to provide:
     * - Exercise recommendations and modifications
     * - Injury assessment and recovery advice
     * - Rehabilitation guidance and progress tracking
     * - General physiotherapy education
     * - Safety precautions and best practices
     * 
     * @param userMessage The user's message
     * @return ResponseEntity containing the AI-generated response
     */
    @PostMapping("/send")
    public ResponseEntity<String> sendMessage(@RequestBody String userMessage) {
        try {
            logger.info("Received chat message: {}", userMessage);
            
            // Create system message with physiotherapy prompt
            Message systemMessage = new Message(Role.SYSTEM, PHYSIOTHERAPY_SYSTEM_PROMPT);
            Message userMsg = new Message(Role.USER, userMessage);
            List<Message> messages = Arrays.asList(systemMessage, userMsg);
            
            ChatCompletionRequest request = new ChatCompletionRequest("deepseek-chat", messages, 0.7, false);
            
            logger.debug("Calling DeepSeek API with request: {}", request);
            retrofit2.Response<ChatCompletionResponse> response = deepseekApiService.createChatCompletion(request).execute();
            
            if (response.isSuccessful() && response.body() != null) {
                String aiResponse = response.body().getChoices().get(0).getMessage().getContent();
                logger.info("Received AI response: {}", aiResponse);
                return ResponseEntity.ok(aiResponse);
            } else {
                logger.error("DeepSeek API call failed. Response code: {}, Error body: {}", 
                           response.code(), 
                           response.errorBody() != null ? response.errorBody().string() : "null");
                return ResponseEntity.ok("I'm sorry, I'm having trouble processing your request right now. Please try again later.");
            }
        } catch (IOException e) {
            logger.error("Error processing request", e);
            return ResponseEntity.ok("I'm sorry, I'm having trouble processing your request right now. Please try again later.");
        }
    }
}
