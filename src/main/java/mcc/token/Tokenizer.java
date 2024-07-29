package mcc.token;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Tokenizer {

    public ArrayList<Token> getTokens(String input) {
        ArrayList<Token> allTokens = new ArrayList<>();
        int currentLine = 1;

        StringBuilder patternBuilder = new StringBuilder();
        for (TokenType tokenType : TokenType.values()) {
            if (tokenType.getPattern() != null) {
                if (!patternBuilder.isEmpty()) {
                    patternBuilder.append("|");
                }
                patternBuilder.append("(?<").append(tokenType.name().replace("_", "")).append(">")
                        .append(tokenType.getPattern()).append(")");
            }
        }

        Pattern TOKEN_PATTERNS = Pattern.compile(patternBuilder.toString());
        Matcher matcher = TOKEN_PATTERNS.matcher(input);

        int lastEnd = 0;
        while (matcher.find()) {
            // Update current line number based on the characters between the last match and the current match
            String between = input.substring(lastEnd, matcher.start());
            for (char c : between.toCharArray()) {
                if (c == '\n') {
                    currentLine++;
                }
            }
            lastEnd = matcher.start();

            if (matcher.group(TokenType.COMMENT.name()) != null) {
                continue; // Skip comments
            }
            if (matcher.group(TokenType.WHITESPACE.name()) != null) {
                continue; // Skip whitespace
            }

            boolean matched = false;
            for (TokenType tokenType : TokenType.values()) {
                if (matcher.group(tokenType.name().replace("_","")) != null) {
                    allTokens.add(new Token(tokenType, matcher.group(tokenType.name().replace("_","")), currentLine));
                    matched = true;
                    break;
                }
            }

            if (!matched) {
                allTokens.add(new Token(TokenType.UNKNOWN, matcher.group(), currentLine));
            }
        }

        allTokens.add(new Token(TokenType.END_OF_FILE, "END_OF_FILE", currentLine));

        return allTokens;
    }
}
