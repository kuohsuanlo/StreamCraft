package cf.catworlds.streamcraft.core.text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormater {

	private class SingleText {
		private final String[] msgArray;
		private final int[] replaceArray;

		private SingleText(String[] msgArray, int[] replaceArray) {
			this.msgArray = msgArray;
			this.replaceArray = replaceArray;
		}

		public String format(Object... args) {
			StringBuilder output = new StringBuilder();
			for (int i = 0; i < msgArray.length; i++) {
				int keyID = replaceArray[i];
				if (keyID > -1 && keyID < args.length && args[keyID] != null)
					output.append(args[keyID]);
				else
					output.append(msgArray[i]);
			}
			return output.toString();
		}
	}

	private SingleText[] texts;

	public TextFormater(List<String> rawStrings) {
		this(rawStrings, new ArrayList<>());
	}

	public TextFormater(List<String> rawStrings, List<String> keys) {
		keys.replaceAll(key -> Pattern.quote(key));

		List<SingleText> textList = new ArrayList<>();

		for (final String rawText : rawStrings) {
			List<String> work = new ArrayList<>();
			List<Integer> workR = new ArrayList<>();
			List<String> split = new ArrayList<>();
			List<Integer> splitR = new ArrayList<>();
			work.add(rawText);
			workR.add(-1);

			for (int keyNumber = 0; keyNumber < keys.size(); keyNumber++) {
				Pattern keyPattern = Pattern.compile(
						"\\$\\{" + keys.get(keyNumber) + "(,(?!\\})((((?<!\\\\)\\\\(\\\\\\\\)*\\})|[^}])+?))?\\}");

				split.clear();
				splitR.clear();
				Iterator<String> itS = work.iterator();
				Iterator<Integer> itR = workR.iterator();
				while (itS.hasNext()) {
					final String str = itS.next();
					final int strR = itR.next();
					if (strR != -1) {
						split.add(str);
						splitR.add(strR);
						continue;
					}
					findKey(str, keyPattern, keyNumber, split, splitR);
				} // end of while (each part : work)
				work.clear();
				workR.clear();
				work.addAll(split);
				workR.addAll(splitR);
			} // end of for (keyNumber : keys)

			String[] msgArray = work.stream().toArray(size -> new String[size]);
			int[] replaceArray = workR.stream().mapToInt(i -> i).toArray();

			textList.add(new SingleText(msgArray, replaceArray));

		} // end of for (rawText : rawStrings)

		this.texts = textList.toArray(new SingleText[textList.size()]);

	}

	private void findKey(String str, Pattern keyPattern, int keyNumber, List<String> strAddTo, List<Integer> intAddTo) {
		// find "${key}" or "${key,Default}" (use '\' to escape '}')
		Matcher mat = keyPattern.matcher(str);

		// split and get key
		int pointer;
		for (pointer = 0; mat.find(); pointer = mat.end()) {
			if (pointer != mat.start()) {
				strAddTo.add(str.substring(pointer, mat.start()));
				intAddTo.add(-1);
			}
			String findKey = mat.group();
			int def_start = findKey.indexOf(',');
			if (def_start != -1) // has default value
				findKey = findKey.substring(def_start, findKey.length() - 1).replaceAll("\\\\(.)", "$1");
			else
				findKey = "";
			strAddTo.add(findKey);
			intAddTo.add(keyNumber);
		}
		if (pointer != str.length()) {
			strAddTo.add(str.substring(pointer));
			intAddTo.add(-1);
		}
	}

	public String format(Object... args) {
		if (texts == null || texts.length == 0)
			return "";
		return texts[new Random().nextInt(texts.length)].format(args);
	}

}
