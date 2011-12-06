package library;

import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.PlainDocument;
import java.lang.Character;

/**
 * This class makes sure people can only insert numbers 0-9 into any TextField
 * that is constructed with it.
 * 
 * @author Nicholas Eddy, Mike Novak, Kyohei Mizokami, Chris Panzero
 * 
 */
public class NumberDocument extends PlainDocument {

	private static final long serialVersionUID = 1L;

	public NumberDocument() {
		super();
	}

	public void insertString(int offs, String str, AttributeSet a)
			throws BadLocationException {
		if (str == null) {
			return;
		}
		char[] digits = new char[str.length()];
		int length = 0;
		for (int i = 0; i < str.length(); i++) {
			char ch = str.charAt(i);
			if (Character.isDigit(ch)) {
				digits[length++] = ch;
			}
			super.insertString(offs, new String(digits, 0, length), a);
		}
	}
}