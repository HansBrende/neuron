package gui;

import java.util.function.BooleanSupplier;

import javafx.scene.control.TextField;

public class FXUtil {
	
	public static final TextBuilder DECIMAL = (sb, from, to, s)-> {
		s = s.replaceAll("[^\\d\\.]", "");
		if (sb.indexOf(".") != -1)
			s = s.replace(".", "");
		else {
			int index = s.indexOf(".");
			if (index != -1) {
				String first = s.substring(0, index + 1);
				String second = s.substring(index + 1);
				second = second.replace(".", "");
				s = first + second;
			}
		}
		return s;
	};
	
	public static interface TextBuilder {
		String build(String existingContent, int from, int to, String replacement);
		
		
	}
	
	
	public static TextField formattedTextField(boolean overwrite, TextBuilder b, BooleanSupplier switchedOn) {
		return new TextField() {
			public void replaceText(int from, int to, String replacement) {
				if (switchedOn.getAsBoolean()) {
					String content = getText();
					int contentLen = content.length();
					if (from < 0 || to < from || to > contentLen)
						throw new IllegalArgumentException("size: " + contentLen + ", from: " + from + ", to: " + to);
					int insertEnd = from + replacement.length();
					if (overwrite)
						to = Math.min(contentLen, Math.max(insertEnd, to));
					super.replaceText(from, to, b.build(content, from, to, replacement));
					this.positionCaret(insertEnd);
				} else {
					super.replaceText(from, to, replacement);
				}
			}
		};
	}
	
	

}
