package javadoc;

/**
 * <a href="https://docs.oracle.com/en/java/javase/17/docs/specs/javadoc/doc-comment-spec.html">Documentation of
 * javadoc</a> makes it clear that Javadoc tag "link" can only be used to link to:
 * "the documentation for the specified module, package, class, or member name of a referenced class".
 * <p>
 * However, it is also possible to "link" to type parameters: {@link TYPE}.
 * When Javadoc is rendered as HTML the "link" to the type parameter in the
 * previous sentence gets converted into a link to the <em>class</em>.
 *
 * @param <TYPE> The type of element. This is the actual documentation of the
 *               type parameter
 */
public class DemonstrationOfTypeParameterLinks<TYPE> {
	/**
	 * <p>
	 * {@code @param element} is missing below, so javadoc gives
	 * a warning:
	 * <pre>
	 * .../DemonstrationOfTypeParameterLinks.java:23: warning: no @param for element ...
	 * </pre>
	 * <p>
	 * We aren't allowed to link to the {@code parameter}, javadoc gives
	 * an error:
	 * <pre>
	 * .../DemonstrationOfTypeParameterLinks.java:16: error: reference not found
	 * </pre>
	 * <p>
	 * When "linking" to the type parameter {@link E} of the method or type
	 * parameter {@link TYPE} of the class, the rendered HTML instead contains
	 * links to the class {@link DemonstrationOfTypeParameterLinks}.
	 * <p>
	 * It would be preferable, if {@code javadoc} either:
	 * <ul>
	 *     <li>complained about such non-working {@code {@link}} tags</li>
	 *     <li>or actually allowed linking to type parameters</li>
	 * </ul>
	 *
	 * @param parameter the parameter of this method
	 * @param <E>       the type of the element
	 */
	public <E> void methodName(int parameter, E element) {
	}
}
