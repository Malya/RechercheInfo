package format.stemmer;

import java.text.Normalizer;
import java.util.Map.Entry;

import format.Token;
import format.automaton.Automaton;
import format.automaton.Backward;

public class Stem implements Token, CharSequence {

	/***									 'a'   'b'    'c'    'd'    'e'   'f'    'g'    'h'    'i'   'j'    'k'    'l'    'm'    ***/
	private static final boolean vowel[] = {true, false, false, false, true, false, false, false, true, false, false, false, false,
	/***									 'n'    'o'   'p'    'q'    'r'    's'    't'    'u'   'v'    'w'    'x'    'y'   'z'    ***/
											false, true, false, false, false, false, false, true, false, false, false, true, false};
	
	private static boolean isVowel(char c) {
		if ((c >= 'a') && (c <= 'z')) {
			return vowel[c-'a'];
		} else {
			return false;
		}
	}
	
	private static final char col[] = {'c', 'o', 'l'};
	private static final char par[] = {'p', 'a', 'r'};
	private static final char tap[] = {'t', 'a', 'p'};
	private static final char ic[] = {'i', 'c'};
	private static final char iv[] = {'i', 'v'};
	private static final char at[] = {'a', 't'};
	private static final char eus[] = {'e', 'u', 's'};
	private static final char abl[] = {'a', 'b', 'l'};
	private static final char iqU[] = {'i', 'q', 'U'};
	private static final char ier[] = {'i', 'e', 'r'};
	private static final char Ier[] = {'I', 'e', 'r'};
	private static final char abil[] = {'a', 'b', 'i', 'l'};
	private static final char enn[] = {'e', 'n', 'n'};
	private static final char onn[] = {'o', 'n', 'n'};
	private static final char ett[] = {'e', 't', 't'};
	private static final char ell[] = {'e', 'l', 'l'};
	private static final char eill[] = {'e', 'i', 'l', 'l'};
	
	private static final int NONE = 0;
	private static final int ALTER = 1;
	private static final int SPEC = 2;
			
	private static final String[][] suffix = {{"ance", "iqUe", "isme", "able", "iste", "eux", "ances", "iqUes", "ismes", "ables", "istes"},
											  {"atrice", "ateur", "ation", "atrices", "ateurs", "ations"},
											  {"logie", "logies"},
											  {"usion", "ution", "usions", "utions"},
											  {"ence", "ences"},
											  {"ement", "ements"},
											  {"ite", "ites"},
											  {"if", "ive", "ifs", "ives"},
											  {"eaux"},
											  {"aux"},
											  {"euse", "euses"},
											  {"issements", "issements"},
											  {"amment"},
											  {"emment"},
											  {"ment", "ments"}};
	private static final Automaton smatcher = new Backward(suffix);
	private static final String[][] verb = {{"imes", "it", "ites", "i", "ie", "ies", "ir", "ira", "irai", "iraIent", "irait",
											 "iras", "irent", "irez", "iriez", "irions", "irons", "iront", "is", "issaIent",
											 "issais", "issait", "issant", "issante", "issants", "isse",
											 "issent", "isses", "issez", "issiez", "issions", "issons", "it"},
		  									{"ions"},
		  									{"e", "ee", "es", "erent", "ent", "er", "era", "erai", "eraIent", "erais", "erait",
		  									 "eras", "erez", "eriez", "erions", "erons", "eront", "ez", "iez"},
		  									{"ames", "at", "ates", "a", "ai", "aIent", "ais", "ait", "ant", "ante", "antes", "ants",
		  									 "as", "asse", "assent", "asses", "assiez", "assions"}};
	private static final Automaton vmatcher = new Backward(verb);
	private static final String[][] residual = {{"ion"},
											    {"ier", "iere", "Ier", "Iere"},
											    {"e"}};
	private static final Automaton rmatcher = new Backward(residual);
	
	private char seq[];
	private int begin;
	private int end;
	private Integer rv;
	private Integer r1;
	private Integer r2;
	private String stem;
	private String normal;
	private String tag;
	
	protected Stem(String word) {
		this.normal = this.init(word);
		if (this.end > 1) {
			this.stem = this.stem();
		} else {
			this.stem = this.normal;
		}
	}
	
	private String init(String word) {
		String normal = Normalizer.normalize(word, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
		this.seq = normal.toCharArray();
		this.begin = 0;
		this.end = this.seq.length;
		return normal;
	}
	
	private String stem() {
		this.consonant();
		this.regions();
		int s = this.suffixStandardRemoval();
		int v = NONE;
		if (s != ALTER) {
			v = this.suffixVerbRemoval();
		}
		if ((s != NONE) || (v != NONE)) {
			if (this.endWith('Y')) {
				this.seq[this.end-1] = 'i';
			}
		} else {
			this.suffixResidualRemoval();
		}
		if (this.endWith(enn) || this.endWith(onn) || this.endWith(ett) || this.endWith(ell) || this.endWith(eill)) {
			this.end -= 1;
		}
		StringBuilder sb = new StringBuilder(this.end);
		for (int i = 0; i < this.end; i += 1) {
			sb.append(Character.toLowerCase(this.seq[i]));
		}
		this.rv = null;
		this.r1 = null;
		this.r2 = null;
		return sb.toString();
	}
	
	@Override
	public char charAt(int at) {
		if ((at >= this.begin) && (at < this.end)) {
			return this.seq[at];
		} else {
			return '\0';
		}
	}
	
	private boolean equals(int at, char c) {
		return this.charAt(at) == c;
	}

	private boolean precededBy(int at, char c) {
		return this.equals(at-1, c);
	}

	private boolean endWith(char c) {
		return equals(this.end-1, c);
	}
	
	private boolean equals(int at, char seq[]) {
		for (int i = 0; i < seq.length; i += 1) {
			if (!this.equals(i+at, seq[i])) {
				return false;
			}
		}
		return true;
	}
	
	private boolean beginWith(char seq[]) {
		return equals(0, seq);
	}
		
	private boolean precededBy(int at, char seq[]) {
		return this.equals(at-seq.length, seq);
	}
	
	private boolean endWith(char seq[]) {
		return equals(this.end-seq.length, seq);
	}
	
	private void substitute(int at, char c) {
		if ((at >= this.begin) && (at < this.end)) {
			this.seq[at] = c;
		}
	}
	
	private void substitute(char c) {
		this.substitute(this.end-1, c);
	}
	
	private void substitute(int at, CharSequence seq) {
		for (int i = 0; i < seq.length(); i += 1) {
			this.substitute(at - seq.length() + 1 + i, seq.charAt(i));
		}
	}

	private void substitute(CharSequence seq) {
		this.substitute(this.end-1, seq);
	}
	
	private void consonant() {
		this.seq[this.end - 1] = Character.toLowerCase(this.seq[this.end - 1]);
		for (int i = 0; i < this.end - 1; i += 1) {
			this.seq[i] = Character.toLowerCase(this.seq[i]);
			switch (this.seq[i]) {
			case 'u':
				if (this.equals(i-1, 'q')) {
					this.seq[i] = 'U';
					break;
				}
			case 'i':
				if (isVowel(this.seq[i+1])) {
					if (isVowel(this.charAt(i-1))) {
						this.seq[i] = Character.toUpperCase(this.seq[i]);
						break;
					}
				}
				break;
			case 'y':
				if (isVowel(this.seq[i+1])) {
					this.seq[i] = Character.toUpperCase(this.seq[i]);
					break;
				} else if (isVowel(this.charAt(i-1))) {
					this.seq[i] = Character.toUpperCase(this.seq[i]);
					break;
				}
			default:
			}
		}
	}

	private void regions() {
		this.rv = null;
		this.r1 = null;
		this.r2 = null;
		if (this.beginWith(col) || this.beginWith(par) || this.beginWith(tap)) {
			this.rv = 3;
			this.r1 = 3;
		} else if (isVowel(this.seq[0])) {
			if (isVowel(this.seq[1])) {
				this.rv = 3;
				this.r1 = 3;
			} else {
				this.r1 = 2;
				for (int i = 2; i < this.end; i += 1) {
					if (isVowel(this.seq[i])) {
						this.rv = i + 1;
						break;
					}
				}
			}
		} else {
			for (int i = 1; i < this.end; i += 1) {
				if ((this.rv == null) && isVowel(this.seq[i])) {
					this.rv = i + 1;
				} else if ((this.rv != null) && !isVowel(this.seq[i]) && isVowel(this.charAt(i-1))) {
					this.r1 = i + 1;
					break;
				}
			}
		}
		if (this.r1 != null) {
			for (int i = this.r1 + 1; i < this.end - 1; i += 1) {
				if (!isVowel(this.seq[i]) && isVowel(this.charAt(i-1))) {
					this.r2 = i + 1;
					break;
				}
			}
		}
		if (this.rv == null) {
			this.rv = this.end;
		}
		if (this.r1 == null) {
			this.r1 = this.end;
		}
		if (this.r2 == null) {
			this.r2 = this.end;
		}
	}

	private boolean onRV(int pos) {
		return pos >= this.rv;
	}
	
	private boolean onR1(int pos) {
		return pos >= this.r1;
	}
	
	private boolean onR2(int pos) {
		return pos >= this.r2;
	}
	
	private int suffixStandardRemoval() {
		Entry<Integer, Integer> match = smatcher.match(this, 0, this.end);
		if (match != null) {
			int pos = match.getValue();
			switch (match.getKey()) {
			case 0:
				if (this.onR2(pos)) {
					this.subSequence(pos);
					return ALTER;
				}
				break;
			case 1:
				if (this.onR2(pos)) {
					this.subSequence(pos);
					if (this.precededBy(pos, ic)) {
						if (this.onR2(pos-2)) {
							this.subSequence(pos-2);
						} else {
							this.subSequence(pos+1);
							this.substitute("qU");
						}
					}
					return ALTER;
				}
				break;
			case 2:
				if (this.onR2(pos)) {
					this.subSequence(pos+3);
					return ALTER;
				}
				break;
			case 3:
				if (this.onR2(pos)) {
					this.subSequence(pos+1);
					return ALTER;
				}
				break;
			case 4:
				if (this.onR2(pos)) {
					this.subSequence(pos+3);
					this.substitute('t');
					return ALTER;
				}
				break;
			case 5:
				if (this.onRV(pos)) {
					this.subSequence(pos);
					if (this.precededBy(pos, iv)) {
						if (this.onR2(pos-2)) {
							this.subSequence(pos-2);
							if (this.precededBy(pos-2, at)) {
								this.subSequence(pos-4);
							}
						}
					} else if (this.precededBy(pos, eus)) {
						if (this.onR2(pos-3)) {
							this.subSequence(pos-3);
						} else if (this.onR1(pos-3)) {
							this.substitute('x');
						}
					} else if (this.precededBy(pos, abl) || this.precededBy(pos, iqU)) {
						if (this.onR2(pos-3)) {
							this.subSequence(pos-3);
						}
					} else if (this.precededBy(pos, ier) || this.precededBy(pos, Ier)) {
						if (this.onRV(pos-3)) {
							this.subSequence(pos-2);
							this.substitute('i');
						}
					}
					return ALTER;
				}
				break;
			case 6:
				if (this.onR2(pos)) {
					this.subSequence(pos);
					if (this.precededBy(pos, abil)) {
						if (this.onR2(pos-4)) {
							this.subSequence(pos-4);
						} else {
							this.subSequence(pos-1);
							this.substitute('l');
						}
					} else if (this.precededBy(pos, ic)) {
						if (this.onR2(pos-2)) {
							this.subSequence(pos-2);
						} else {
							this.subSequence(pos+1);
							this.substitute("qU");
						}
					} else if (this.precededBy(pos, iv)) {
						if (this.onR2(pos-2)) {
							this.subSequence(pos-2);
						}
					}
					return ALTER;
				}
				break;
			case 7:
				if (this.onR2(pos)) {
					this.subSequence(pos);
					if (this.precededBy(pos, at)) {
						if (this.onR2(pos-2)) {
							this.subSequence(pos-2);
							if (this.precededBy(pos-2, ic)) {
								if (this.onR2(pos-4)) {
									this.subSequence(pos-4);
								} else {
									this.subSequence(pos-1);
									this.substitute("qU");
								}
							}
						}
					}
					return ALTER;
				}
				break;
			case 8:
				this.subSequence(pos+3);
				return ALTER;
			case 9:
				if (this.onR1(pos)) {
					this.subSequence(pos+2);
					this.substitute('l');
				}
				return ALTER;
			case 10:
				if (this.onR2(pos)) {
					this.subSequence(pos);
					return ALTER;
				} else if (this.onR1(pos)) {
					this.subSequence(pos+3);
					this.substitute('x');
					return ALTER;
				}
				break;
			case 11:
				if (this.onR1(pos)) {
					if (!isVowel(this.charAt(pos-1))) {
						this.subSequence(pos);
						return ALTER;
					}
				}
				break;
			case 12:
				if (this.onRV(pos)) {
					this.subSequence(pos+3);
					this.substitute("nt");
					return SPEC;
				}
				break;
			case 13:
				if (this.onRV(pos)) {
					this.subSequence(pos+3);
					this.substitute("nt");
					return SPEC;
				}
				break;
			case 14:
				if (this.onRV(pos-1)) {
					if (isVowel(this.charAt(pos-1))) {
						this.subSequence(pos);
						return SPEC;
					}
				}
				break;
			}
		}
		return NONE;
	}
	
	private int suffixVerbRemoval() {
		Entry<Integer, Integer> match = vmatcher.match(this, this.rv, this.end);
		if (match != null) {
			int pos = match.getValue();
			switch (match.getKey()) {
			case 0:
				if (this.onRV(pos-1)) {
					if (!isVowel(this.charAt(pos-1))) {
						this.subSequence(pos);
						return ALTER;
					}
				}
				break;
			case 1:
				if (this.onR2(pos)) {
					this.subSequence(pos);
					return ALTER;
				}
				break;
			case 2:
				if (this.onRV(pos)) { 
					this.subSequence(pos);
					return ALTER;
				}
				break;
			case 3:
				if (this.onRV(pos)) {
					this.subSequence(pos);
					if (this.precededBy(pos, 'e')) {
						if (this.onRV(pos-1)) {
							this.subSequence(pos-1);
						}
					}
					return ALTER;
				}
				break;
			}
		}
		return NONE;
	}
	
	private void suffixResidualRemoval() {
		if (this.endWith('s')) {
			char c = this.charAt(this.end-2);
			if ((c != 'a') && (c != 'i') && (c != 'o') && (c != 'u') && (c != 'e') && (c != 's')) {
				this.subSequence(this.end-1);
			}
		}
		Entry<Integer, Integer> match = rmatcher.match(this, this.rv, this.end);
		if (match != null) {
			int pos = match.getValue();
			switch (match.getKey()) {
			case 0:
				if (this.onR2(pos)) {
					if (this.onRV(pos-1)) {
						if (this.precededBy(pos, 's') || this.precededBy(pos, 't')) {
							this.subSequence(pos-1);
						}
					}
				}
				break;
			case 1:
				this.subSequence(pos+1);
				break;
			case 2:
				this.subSequence(pos);
				break;
			}
		}
	}
	
	protected void setRoot(String root) {
		this.stem = root;
	}
	
	public String getRoot() {
		return this.stem;
	}
	
	
	public String toString() {
		return this.normal;
	}

	@Override
	public int length() {
		return this.end - this.begin;
	}

	@Override
	public CharSequence subSequence(int begin, int end) {
		this.begin += begin;
		this.end = end + begin;
		return this;
	}

	public CharSequence subSequence(int end) {
		this.end = end + this.begin;
		return this;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o != null) {
			if (o instanceof Stem) {
				Stem s = (Stem) o;
				return this.normal.equals(s.normal);
			}
		}
		return false;
	}
	
	@Override
	public int hashCode() {
		return this.normal.hashCode();
	}

	@Override
	public void setTag(String tag) {
		this.tag = tag;		
	}

	@Override
	public String getTag() {
		return tag;
	}
	
}
