package fi.tv7.taivastv7.helpers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fi.tv7.taivastv7.R;

public class KeyboardChars {
    public static final List<SearchCharacter> SEARCH_CHARACTER_ROW_1 = new ArrayList<>(
        Arrays.asList(
            new SearchCharacter(R.id.k_0_0,'q', 'Q','1'),
            new SearchCharacter(R.id.k_0_1,'w', 'W','2'),
            new SearchCharacter(R.id.k_0_2,'e', 'E','3'),
            new SearchCharacter(R.id.k_0_3,'r', 'R','4'),
            new SearchCharacter(R.id.k_0_4,'t', 'T','5'),
            new SearchCharacter(R.id.k_0_5,'y', 'Y','6'),
            new SearchCharacter(R.id.k_0_6,'u', 'U','7'),
            new SearchCharacter(R.id.k_0_7,'i', 'I','8'),
            new SearchCharacter(R.id.k_0_8,'o', 'O','9'),
            new SearchCharacter(R.id.k_0_9,'p', 'P','0'),
            new SearchCharacter(R.id.k_0_10,'å', 'Å',',')
        )
    );

    public static final List<SearchCharacter> SEARCH_CHARACTER_ROW_2 = new ArrayList<>(
        Arrays.asList(
            new SearchCharacter(R.id.k_1_0,'a', 'A', '.'),
            new SearchCharacter(R.id.k_1_1,'s', 'S', ';'),
            new SearchCharacter(R.id.k_1_2,'d', 'D', ':'),
            new SearchCharacter(R.id.k_1_3,'f', 'F', '!'),
            new SearchCharacter(R.id.k_1_4,'g', 'G', '='),
            new SearchCharacter(R.id.k_1_5,'h', 'H', '/'),
            new SearchCharacter(R.id.k_1_6,'j', 'J', '('),
            new SearchCharacter(R.id.k_1_7,'k', 'K', ')'),
            new SearchCharacter(R.id.k_1_8,'l', 'L', '['),
            new SearchCharacter(R.id.k_1_9,'ö', 'Ö', ']'),
            new SearchCharacter(R.id.k_1_10,'ä', 'Ä', '-')
        )
    );

    public static final List<SearchCharacter> SEARCH_CHARACTER_ROW_3 = new ArrayList<>(
        Arrays.asList(
            new SearchCharacter(R.id.k_2_0,'z', 'Z', '_'),
            new SearchCharacter(R.id.k_2_1,'x', 'X', '*'),
            new SearchCharacter(R.id.k_2_2,'c', 'C', '>'),
            new SearchCharacter(R.id.k_2_3,'v', 'V', '<'),
            new SearchCharacter(R.id.k_2_4,'b', 'B', '#'),
            new SearchCharacter(R.id.k_2_5,'n', 'N', '?'),
            new SearchCharacter(R.id.k_2_6,'m', 'M', '+'),
            new SearchCharacter(R.id.k_2_7,null, null, '&'),
            new SearchCharacter(R.id.k_2_8,null, null, null),
            new SearchCharacter(R.id.k_2_9,null, null, null),
            new SearchCharacter(R.id.k_2_10,null, null, null)
        )
    );

    public static class SearchCharacter {
        private int id = 0;
        private Character lowercase = null;
        private Character uppercase = null;
        private Character special = null;

        public SearchCharacter(int id, Character lowercase, Character uppercase, Character special) {
            this.id = id;
            this.lowercase = lowercase;
            this.uppercase = uppercase;
            this.special = special;
        }

        public int getId() {
            return id;
        }

        public Character getLowercase() {
            return lowercase;
        }

        public Character getUppercase() {
            return uppercase;
        }

        public Character getSpecial() {
            return special;
        }
    }
}
