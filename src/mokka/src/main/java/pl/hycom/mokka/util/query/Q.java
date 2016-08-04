package pl.hycom.mokka.util.query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.Transformer;
import org.apache.commons.lang3.StringUtils;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Utilowa klasa pomocnicza wspierajaca budowe zapytan SQL. Klasa wiaze zapytanie od razu z parametrami
 * dzieki czemu poprawia czytelnosc zapytan sql. Dodatkowo ma metody wspomagajace generowanie kodow SQL
 * dzieki czemu mozna skrocic i ujednolicic np sprawdzanie nieustawienia flagi.
 *
 * Zapytanie zaczynamy budowac zawsze od:
 * Q q = Q.select("pole, pole")...
 *
 * Wiekszosc metod zwraca siebie co pomaga 'chainowac' zapytani, np:
 *
 * Q q = Q.select("foo").from("bar").where("foo = ?, 1).and(Q.before("start", new Date());
 *
 * Kontrakt: Metody na obiekcie zawsze modyfikuja biezacy obiekt i po modyfikacji go zwracaja, nigdy
 * nie generuja nowego obiektu, do tego sa przeznaczone tylko metody statyczne, wyjatkiem jest metoda clone/copy()
 * zwracajaca kopie biezacego obiektu
 *
 * @author mariusz hagi (hagi@amg.net.pl)
 * @id CL-1030.013.084
 */
public class Q {

	protected String select = StringUtils.EMPTY;
	protected String from = StringUtils.EMPTY;
	protected String orderby = StringUtils.EMPTY;
	protected String groupby = StringUtils.EMPTY;
	protected Integer startingIndex = null;
	protected Integer maxResults = null;

	protected StringBuilder where = new StringBuilder();
	protected List<Object> params = new ArrayList<>();

	/** Flaga okreslajaca, ze jest to podzapytanie, nie dodajemy wtedy select i from przy generowaniu query */
	protected boolean subquery = false;

	private Q() {
	} // disabled

	private Q(String where) {
		this.where.append(where);
		this.subquery = true;
	}

	private Q(String where, Object... params) {
		this.where.append(where);
		this.params.addAll(Arrays.asList(params));
		this.subquery = true;
	}

	public static Q select(String query) {
		Q q = new Q();
		q.select = query;
		return q;
	}

	public static Q empty() {
		Q q = new Q();
		q.subquery = true;

		return q;
	}

	/**
	 * sekcja FROM zapytania, mozna uzyc wielokrotnie np. q.from("foo).from("bar")
	 * polecam uzywanie krotkich aliasow przy kilku tabelach, np: q.from("v_declare_price_p v, dcs_shop_proposition p")
	 * i uzywanie pozniej aliasow przy polach dla zwiekszenia czytelnosci
	 *
	 * @param from
	 * @return
	 */
	public Q from(String... from) {
		this.from += (this.from.length() > 0 ? ", " : StringUtils.EMPTY) + StringUtils.join(from, ", ");
		return this;
	}

	/**
	 * sekcja ORDER BY zapytania, mozna uzyc wielokrotnie np. q.orderby("foo).orderby("bar", "blah desc")
	 *
	 * @param orderbys
	 *            okresla po czym sortowac
	 * @return biezacy obiekt, ulatwia 'chainowanie'
	 */
	public Q orderby(String... orderbys) {
		this.orderby += (this.orderby.length() > 0 ? ", " : StringUtils.EMPTY) + StringUtils.join(orderbys, ", ");
		return this;
	}

	/**
	 * sekcja ORDER BY z parametrami, ze wzgledu na dodawanie parametrow, powinna byc wywolywana w naturalnym porzadku
	 * (statyczne orderby moze byc dolaczane wczesniej), czyli
	 * q.and(...).orderby(...)
	 * a nie q.orderby(...).and(...)
	 *
	 * @param orderby
	 *            okresla po czym sortowac
	 * @param params
	 *            wartosci parametrow dla orderby
	 * @return biezacy obiekt, ulatwia 'chainowanie'
	 */
	public Q orderbyWithParams(String orderby, Object... params) {
		this.orderby += (this.orderby.length() > 0 ? ", " : StringUtils.EMPTY) + orderby;
		this.params.addAll(Arrays.asList(params));
		return this;
	}

	/**
	 * sekcja GROUP BY zapytania
	 *
	 * @param groupby
	 *            okresla po czym grupować
	 * @return biezacy obiekt, ulatwia 'chainowanie'
	 */
	public Q groupby(String groupby) {
		this.groupby = groupby;
		return this;
	}

	public Q and(String query, Object... params) {
		append(" AND ", query, params);
		return this;
	}

	public Q and(Q... q) {
		for (int i = 0; i < q.length; i++) {
			append(" AND ", q[i].query(), q[i].params());
		}
		return this;
	}

	public Q or(String query, Object... params) {
		append(" OR ", query, params);
		return this;
	}

	public Q or(Q q) {
		append(" OR ", q.query(), q.params());
		return this;
	}

	public Q where(String query, Object... params) {
		append(" ", query, params);
		return this;
	}

	public Q where(Q q) {
		append(" ", q.query(), q.params());
		return this;
	}

	/**
	 * Zagniedzone ANDy: Q1 AND Q2 AND Q3... np:
	 * q.where(Q.ands(....).or(Q.ands(...))
	 *
	 * @param qs
	 * @return
	 */
	public static Q ands(Q... qs) {
		Q q = new Q(qs[0].query(), qs[0].params());
		if (qs.length > 1) {
			for (int i = 1; i < qs.length; i++) {
				q.and(qs[i]);
			}
		}
		return q;
	}

	/**
	 * Nested ORs: Q1 OR Q2 OR Q3...
	 *
	 * @param qs
	 * @return
	 */
	public static Q ors(Q... qs) {
		Q q = new Q(qs[0].query(), qs[0].params());
		if (qs.length > 1) {
			for (int i = 1; i < qs.length; i++) {
				q.or(qs[i]);
			}
		}
		return q;
	}

	/**
	 * Do generowania podzapytan
	 */
	public static Q stmt(String query, Object... params) {
		return new Q("(" + query + ")", params);
	}

	public static Q exists(Q sub) {
		return new Q("exists (" + sub.query() + ")", sub.params());
	}

	public static Q notExists(Q sub) {
		return new Q("not exists (" + sub.query() + ")", sub.params());
	}

	/**
	 * Do generowania podzapytan
	 */
	public static Q sub(String query, Object... params) {
		return new Q("(" + query + ")", params);
	}

	/**
	 * Generuje: {field} = ?
	 * Raczej zalecane wpisanie wprost (czytelniej):
	 *
	 * q.and("foo = ?", foo)
	 *
	 * ale moze byc przydatne przy zahniezdzaniu:
	 *
	 * q.and(Q.eq("foo", foo).or("blah > 3"))
	 * sql: AND (foo = ? OR blah > 3)
	 *
	 * zamiast: q.and(Q.stmt("foo = ?", foo).or("blah > 3"))
	 */
	public static Q eq(String field, Object value) {
		return new Q(field + " = ?", value);
	}

	/**
	 * Generuje: {field} < ?
	 * Raczej zalecane wpisanie wprost (czytelniej):
	 *
	 * q.and("foo < ?", foo)
	 *
	 * ale moze byc przydatne przy dynamicznych polach
	 *
	 * q.and(Q.ls(foo, 12))
	 * zamiast: q.and(foo + " < ?", 12)
	 */
	public static Q lt(String field, Object value) {
		return new Q(field + " < ?", value);
	}

	/**
	 * Generuje: {field} > ?
	 * Raczej zalecane wpisanie wprost (czytelniej):
	 *
	 * q.and("foo > ?", foo)
	 *
	 * ale moze byc przydatne przy dynamicznych polach
	 *
	 * q.and(Q.ls(foo, 12))
	 * zamiast: q.and(foo + " > ?", 12)
	 */
	public static Q gt(String field, Object value) {
		return new Q(field + " > ?", value);
	}

	/**
	 * Generuje: {field} <= ?
	 * Raczej zalecane wpisanie wprost (czytelniej):
	 *
	 * q.and("foo <= ?", foo)
	 *
	 * ale moze byc przydatne przy dynamicznych polach
	 *
	 * q.and(Q.ls(foo, 12))
	 * zamiast: q.and(foo + " <= ?", 12)
	 */
	public static Q le(String field, Object value) {
		return new Q(field + " <= ?", value);
	}

	/**
	 * Generuje: {field} >= ?
	 * Raczej zalecane wpisanie wprost (czytelniej):
	 *
	 * q.and("foo >= ?", foo)
	 *
	 * ale moze byc przydatne przy dynamicznych polach
	 *
	 * q.and(Q.ls(foo, 12))
	 * zamiast: q.and(foo + " >= ?", 12)
	 */
	public static Q ge(String field, Object value) {
		return new Q(field + " >= ?", value);
	}

	/**
	 * Sprawdzanie nieustawienia flagi: {field} is null OR {field} = 0
	 *
	 * @param field
	 *            nazwa pola
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q not(String field) {
		return new Q("(" + field + " is null OR " + field + " = 0)");
	}

	/**
	 * Sprawdzanie nulla: {field} is null
	 *
	 * @param field
	 *            nazwa pola
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q isNull(String field) {
		return new Q(field + " is null");
	}

	/**
	 * Sprawdzanie nulla dla kazdego z podanych pol: {field1} is null and {field2} is null and ...
	 *
	 * @param fields
	 *            nazwy pol
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q nulls(String... fields) {
		Q q = Q.isNull(fields[0]);
		if (fields.length > 1) {
			for (int i = 1; i < fields.length; i++) {
				q.and(Q.isNull(fields[i]));
			}
		}

		return q;
	}

	/**
	 * Sprawdzanie nulla: {field} is not null
	 *
	 * @param field
	 *            nazwa pola
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q notNull(String field) {
		return new Q(field + " is not null");
	}

	/**
	 * Generuje: {field} like ? %{value}%
	 */
	public static Q like(String field, Object value) {
		return new Q(field + " like ?", StringUtils.replaceChars(value.toString(), '*', '%'));
	}

	/**
	 * Sprawdzanie ustawienia flagi: {field} = 1
	 *
	 * @param field
	 *            nazwa pola
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q is(String field) {
		return new Q(field + " = 1");
	}

	/**
	 * Data przed: {field} <= ?
	 * dla ewentualnego nulla uzyj <code>Q.validFrom</code>
	 *
	 * @param field
	 *            nazwa pola
	 * @param date
	 *            stamp
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q before(String field, Date date) {
		return new Q(field + " <= ?", new Timestamp(date.getTime()));
	}

	/**
	 * Data po: {field} >= ?
	 * dla ewentualnego nulla uzyj <code>Q.validTo</code>
	 *
	 * @param field
	 *            nazwa pola
	 * @param date
	 *            stamp
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q after(String field, Date date) {
		return new Q(field + " >= ?", new Timestamp(date.getTime()));
	}

	/**
	 * Wstawiamy pole okreslajace date waznosci do: {field} is null or {field} >= ?(stamp)
	 * Bez nulla uzyj <code>Q.after</code>
	 *
	 * @param field
	 *            nazwa pola
	 * @param date
	 *            stamp
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q validTo(String field, Date date) {
		return new Q("(" + field + " is null OR " + field + " >= ?)", new Timestamp(date.getTime()));
	}

	/**
	 * Wstawiamy pole okreslajace date waznosci od: {field} is null or {field} <= ?(stamp)
	 * Bez nulla uzyj <code>Q.before</code>
	 *
	 * @param field
	 *            nazwa pola
	 * @param date
	 *            stamp
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q validFrom(String field, Date date) {
		return new Q("(" + field + " is null OR " + field + " <= ?)", new Timestamp(date.getTime()));
	}

	/**
	 * Wstawiamy pole okreslajace date waznosci do: {field} is null or {field} >= ?(stamp)
	 * Bez nulla uzyj <code>Q.after</code>
	 *
	 * @param field
	 *            nazwa pola
	 * @param time
	 *            stamp w milisekundach
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q validTo(String field, long time) {
		return new Q("(" + field + " is null OR " + field + " >= ?)", new Timestamp(time));
	}

	/**
	 * Wstawiamy pole okreslajace date waznosci od: {field} is null or {field} <= ?(stamp)
	 * Bez nulla uzyj <code>Q.before</code>
	 *
	 * @param field
	 *            nazwa pola
	 * @param time
	 *            stamp w milisekundach
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q validFrom(String field, long time) {
		return new Q("(" + field + " is null OR " + field + " <= ?)", new Timestamp(time));
	}

	/**
	 * Wstawiamy pole okreslajace date waznosci do: {field} is null or {field} >= ?(stamp)
	 * Bez nulla uzyj <code>Q.after</code>
	 *
	 * @param field
	 *            nazwa pola
	 * @param stamp
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q validTo(String field, Timestamp stamp) {
		return new Q("(" + field + " is null OR " + field + " >= ?)", stamp);
	}

	/**
	 * Wstawiamy pole okreslajace date waznosci od: {field} is null or {field} <= ?(stamp)
	 * Bez nulla uzyj <code>Q.before</code>
	 *
	 * @param field
	 *            nazwa pola
	 * @param stamp
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q validFrom(String field, Timestamp stamp) {
		return new Q("(" + field + " is null OR " + field + " <= ?)", stamp);
	}

	/**
	 * Wstawiamy pola okreslajace date waznosci od i do:
	 * ({field} is null or {field} <= ?(stamp)) and ({field} is null or {field} >= ?(stamp))
	 * Pojedyncze sprawdzenia: <code>Q.validFrom</code>, <code>
	 * &#64;param fieldFrom pole okreslajace date waznosci od
	 * &#64;param fieldTo  pole okreslajace date waznosci do
	 * &#64;param stamp timestamp
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q valid(String fieldFrom, String fieldTo, Timestamp stamp) {
		return Q.validFrom(fieldFrom, stamp).and(Q.validTo(fieldTo, stamp));
	}

	/**
	 * Wstawia konstrukcje: {field} in (?, ?,...)
	 *
	 * @param field
	 *            nazwa pola
	 * @param items
	 *            lista parametrow
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q in(String field, Collection<? extends Object> items) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < items.size(); i++) {
			sb.append(i > 0 ? ", ?" : "?");
		}

		Q q = new Q(field + " IN (" + sb + ")");
		q.params.addAll(items);

		return q;
	}

	/**
	 * Wstawia bezposrednio wartosci dla konstrukcji IN: {field} in (1, 19, 2,...)
	 * nadaje sie tylko dla listy wartosci numerycznych
	 *
	 * @param field
	 *            nazwa pola
	 * @param items
	 *            lista parametrow
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q directIn(String field, List<? extends Object> items) {
		return new Q(field + " IN (" + StringUtils.join(items, ", ") + ")");
	}

	/**
	 * W zaleznosci od wartosci parametru condition generowane jest zapytanie
	 * sprawdzajace czy flaga jest ustawiona (dla wartosci <code>true</code>):
	 * {field} = 1
	 *
	 * lub czy nie jest (dla wartosci <code>false</code>)
	 * {field} is null OR {field} = 0
	 *
	 * @param field
	 *            nazwa pola
	 * @param condition
	 *            warunek okreslajacy, ktory warunek sql generowac
	 * @return obiekt zawierajacy zapytanie i parametry, nigdy <code>null</code>
	 */
	public static Q conditional(String field, boolean condition) {
		return condition ? Q.is(field) : Q.not(field);
	}

	public static Q contains(String field, String match, boolean ignorecase) {
		return new Q(ignorecase ? "upper(" + field + ") LIKE ?" : field + " LIKE ?", "%" + (ignorecase ? match.toUpperCase(Locale.getDefault()) : match) + "%");
	}

	public static Q notContains(String field, String notMatch, boolean ignorecase) {
		return new Q(ignorecase ? "upper(" + field + ") NOT LIKE ?" : field + " NOT LIKE ?", "%" + (ignorecase ? notMatch.toUpperCase(Locale.getDefault()) : notMatch) + "%");
	}

	protected void append(String prefix, String query, Object... params) {
		where.append(where.length() == 0 ? " " : prefix).append("(").append(query).append(")");
		if (params != null && params.length > 0) {
			List<Object> paramList = Arrays.asList(params);
			CollectionUtils.transform(paramList, new Transformer<Object, Object>() {
				@Override
				public Object transform(Object o) {
					return o != null && o.getClass().isEnum() ? o.toString() : o;
				}
			});
			this.params.addAll(paramList);
		}
	}

	public Q startingIndex(Integer startingIndex) {
		this.startingIndex = startingIndex;
		return this;
	}

	public Q maxResults(Integer maxResults) {
		this.maxResults = maxResults;
		return this;
	}

	/**
	 * Generuje nowy obiekt Q na podstawie swoich wartosci, lista parametrow nie jest lista
	 * klonowanych wartosci.
	 */
	public Q copy() {
		Q n = new Q();
		n.select = select;
		n.from = from;
		n.orderby = orderby;
		n.groupby = groupby;
		n.where.append(where.toString());
		n.params.addAll(paramsAsList());
		n.subquery = subquery;
		return n;
	}

	@Override
	public String toString() {
		return "Query[" + query() + "], params [" + StringUtils.join(params(), ", ") + "]";
	}

	/**
	 * Metoda ma za zadanie przekonwertowac znaczniki '?' lub '?{numer}' na
	 * odpowiednie wynikajace z ilosci parametrow
	 *
	 * @param in
	 * @return
	 */
	private String replaceMarks(String in) { // TODO: better method?
		String[] ins = in.split("\\?\\d*", -1); // gratulacje dla projektanta
		// parametrow splita, ugh
		if (ins.length == 1) {
			return in; // brak markerow
		}

		StringBuilder sb = new StringBuilder(ins[0]);
		for (int i = 1; i < ins.length; i++) {
			sb.append("?" + (i - 1)).append(ins[i]);
		}

		return sb.toString();
	}

	/**
	 * Query dla executa
	 *
	 * @return
	 */
	public String query() {
		return replaceMarks(
				(select.length() > 0 && !subquery ? "SELECT " + select + " " : StringUtils.EMPTY)
						+ (from.length() > 0 && !subquery ? "FROM " + from + " " : StringUtils.EMPTY)
						+ (where.length() > 0 && !subquery ? "WHERE " : StringUtils.EMPTY) + where
						+ (groupby.length() > 0 ? " GROUP BY " + groupby : StringUtils.EMPTY)
						+ (orderby.length() > 0 ? " ORDER BY " + orderby : StringUtils.EMPTY));
	}

	/**
	 * Parametry dla executa
	 *
	 * @return
	 */
	public Object[] params() {
		return params.toArray();
	}

	public List<Object> paramsAsList() {
		return params;
	}

	/**
	 * Query z podstawionymi parametrami. Konwertowane sa parametry typu Timestamp oraz String.
	 *
	 * @return
	 */
	public String queryWithParams() {
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		String sqlToDateFormat = "TO_DATE('%s', 'yyyy-mm-dd HH24:MI:SS')";
		String query = query();
		for (Object p : paramsAsList()) {
			if (p instanceof String) {
				p = "'" + p + "'";
			} else if (p instanceof Timestamp) {
				p = String.format(sqlToDateFormat, dateFormat.format((Timestamp) p));
			}
			query = query.replaceFirst("\\?", String.valueOf(p));
		}
		return query;
	}

	Integer getStartingIndex() {
		return startingIndex;
	}

	Integer getMaxResults() {
		return maxResults;
	}

}
