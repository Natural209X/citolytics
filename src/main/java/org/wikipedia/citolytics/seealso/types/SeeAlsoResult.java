package org.wikipedia.citolytics.seealso.types;

import org.apache.flink.api.java.tuple.Tuple3;

/**
 * SeeAlso result
 * <p/>
 * 0: Article
 * 1: Target
 * 2: Number of "See also" links
 */
@Deprecated
public class SeeAlsoResult extends Tuple3<String, String, Integer> {
}
