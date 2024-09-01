package org.example.zhipu.service.Impl;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.example.zhipu.service.Interface.SpeechToText;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.vosk.LibVosk;
import org.vosk.LogLevel;
import org.vosk.Model;
import org.vosk.Recognizer;

import javax.sound.sampled.UnsupportedAudioFileException;
import java.io.IOException;
import java.io.InputStream;

@Service
public class SpeechToTextImpl implements SpeechToText {

    public String speechToText( MultipartFile file) throws IOException {
        LibVosk.setLogLevel(LogLevel.DEBUG);
        String cleanedTextContent=null;
        // 使用 Vosk 库创建 Model 和 Recognizer 对象
        Model model = new Model("/home/java/templates/vosk-model-small-cn-0.22");
        Recognizer recognizer = new Recognizer(model, 16000);

        try (InputStream ais = file.getInputStream()) {
            byte[] b = new byte[4096];
            int bytesRead;
            while ((bytesRead = ais.read(b)) != -1) {
                recognizer.acceptWaveForm(b, bytesRead);
            }
            String result = recognizer.getFinalResult();
            System.out.println("result"+result);
            JsonObject jsonObject = JsonParser.parseString(result).getAsJsonObject();
            String textContent = jsonObject.get("text").getAsString();
            System.out.println(textContent);
            // 假设 textContent 是从语音识别结果中得到的字符串
            cleanedTextContent = textContent.trim();

            // 替换掉字符串中间的多余空格（一个或多个连续的空格）
            cleanedTextContent = cleanedTextContent.replaceAll("\\s+", "");

            // 如果需要去除换行符，可以使用以下方法
            cleanedTextContent = cleanedTextContent.replaceAll("\\n", "");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (recognizer != null) {
                recognizer.close(); // 确保释放资源
            }
        }
        return cleanedTextContent; // 返回识别的文本内容
    }
}
