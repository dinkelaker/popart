/*
 * This implementation was copied from the Daikon project.
 * http://groups.csail.mit.edu/pag/daikon/
 * And was obtained under the following license at 5th of March 2010,
 * which grants completely free reuse without any restrictions:
 * ===================================
 * 10.2 License
 * 
 * Copyright © 1998-2008 Massachusetts Institute of Technology 
 * 
 * Copyright © 2008-2009 University of Washington 
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a
 * copy of this software and associated documentation files (the “Software”),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions: 
 * 
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software. 
 * 
 * The names and trademarks of copyright holders may not be used in
 * advertising or publicity pertaining to the software without specific
 * prior permission. Title to copyright in this software and any associated
 * documentation will at all times remain with the copyright holders. 
 * 
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL 
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 * ===================================
 */

/*
 * @(#)WeakHashMap.java 1.30 04/02/19
 *
 * Copyright 2004 Sun Microsystems, Inc. All rights reserved.
 * SUN PROPRIETARY/CONFIDENTIAL. Use is subject to license terms.
 */

package de.tud.stg.popart.aspect.extensions.itd;

import java.util.Iterator;
import java.util.Map;
import java.util.AbstractMap;
import java.util.Set;
import java.util.AbstractSet;
import java.util.NoSuchElementException;
import java.util.Collection;
import java.util.AbstractCollection;
import java.util.ConcurrentModificationException;
import java.util.ArrayList;

import java.lang.ref.WeakReference;
import java.lang.ref.ReferenceQueue;


/**
 * This is a modified version of @see{WeakHashMap} from JDK 1.5.
 * This modification uses System.identityHashCode() rather than
 * the object's hash code.  All equality checks are identity checks
 * (==) rather than objet equality (.equals); @see{IdentityHashMap}
 * for more information on the changes made in an identity hash map.
 *
 * A hashtable-based Map implementation with weak
 * keys.  An entry in a WeakIdentityHashMap will
 * automatically be removed when its key is no longer in ordinary use.
 * More precisely, the presence of a mapping for a given key will not
 * prevent the key from being discarded by the garbage collector, that
 * is, made finalizable, finalized, and then reclaimed.  When a key
 * has been discarded its entry is effectively removed from the map,
 * so this class behaves somewhat differently than other Map
 * implementations.
 *
 *  Both null values and the null key are supported. This class has
 * performance characteristics similar to those of the HashMap
 * class, and has the same efficiency parameters of initial capacity
 * and load factor.
 *
 *  Like most collection classes, this class is not synchronized.  A
 * synchronized WeakIdentityHashMap may be constructed using the
 * Collections.synchronizedMap method.
 *
 *  The behavior of the WeakIdentityHashMap class depends
 * in part upon the actions of the garbage collector, so several
 * familiar (though not required) Map invariants do not hold
 * for this class.  Because the garbage collector may discard keys at
 * any time, a WeakIdentityHashMap may behave as though an
 * unknown thread is silently removing entries.  In particular, even
 * if you synchronize on a WeakIdentityHashMap instance and
 * invoke none of its mutator methods, it is possible for the
 * size method to return smaller values over time, for the
 * isEmpty method to return false and then
 * true, for the containsKey method to return
 * true and later false for a given key, for the
 * get method to return a value for a given key but later
 * return null, for the put method to return
 * null and the remove method to return
 * false for a key that previously appeared to be in the map,
 * and for successive examinations of the key set, the value set, and
 * the entry set to yield successively smaller numbers of elements.
 *
 *  Each key object in a WeakIdentityHashMap is stored
 * indirectly as the referent of a weak reference.  Therefore a key
 * will automatically be removed only after the weak references to it,
 * both inside and outside of the map, have been cleared by the
 * garbage collector.
 *
 *  Implementation note: The value objects in a
 * WeakIdentityHashMap are held by ordinary strong
 * references.  Thus care should be taken to ensure that value objects
 * do not strongly refer to their own keys, either directly or
 * indirectly, since that will prevent the keys from being discarded.
 * Note that a value object may refer indirectly to its key via the
 * WeakIdentityHashMap itself; that is, a value object may
 * strongly refer to some other key object whose associated value
 * object, in turn, strongly refers to the key of the first value
 * object.  One way to deal with this is to wrap values themselves
 * within WeakReferences before inserting, as in:
 * m.put(key, new WeakReference(value)), and then unwrapping
 * upon each get.
 *
 * The iterators returned by all of this class's "collection view methods"
 * are fail-fast: if the map is structurally modified at any time after
 * the iterator is created, in any way except through the iterator's own
 * remove or add methods, the iterator will throw a
 * ConcurrentModificationException.  Thus, in the face of concurrent
 * modification, the iterator fails quickly and cleanly, rather than risking
 * arbitrary, non-deterministic behavior at an undetermined time in the
 * future.
 *
 * Note that the fail-fast behavior of an iterator cannot be guaranteed
 * as it is, generally speaking, impossible to make any hard guarantees in the
 * presence of unsynchronized concurrent modification.  Fail-fast iterators
 * throw ConcurrentModificationException on a best-effort basis.
 * Therefore, it would be wrong to write a program that depended on this
 * exception for its correctness:  the fail-fast behavior of iterators
 * should be used only to detect bugs.
 *
 * This class is a member of the
 * 
 * Java Collections Framework.
 *
 * @version     1.30, 02/19/04
 * @author      Doug Lea
 * @author      Josh Bloch
 * @author      Mark Reinhold
 * @since       1.2
 * @see         java.util.HashMap
 * @see         java.lang.ref.WeakReference
 */
//@SuppressWarnings({"unchecked", "rawtypes", "nullness"})
public class GroovyWeakIdentityHashMap
extends AbstractMap<Object,Object>
implements Map<Object,Object> {

	/**
	 * The default initial capacity -- MUST be a power of two.
	 */
	public static final int DEFAULT_INITIAL_CAPACITY = 16;

	/**
	 * The maximum capacity, used if a higher value is implicitly specified
	 * by either of the constructors with arguments.
	 * MUST be a power of two <= 1<<30.
	 */
	public static final int MAXIMUM_CAPACITY = 1 << 30;

	/**
	 * The load fast used when none specified in constructor.
	 */
	public static final float DEFAULT_LOAD_FACTOR = 0.75f;

	/**
	 * The table, resized as necessary. Length MUST Always be a power of two.
	 */
	public /*@Nullable*/ MyEntry[] table;

	/**
	 * The number of key-value mappings contained in this weak hash map.
	 */
	public int size;

	/**
	 * The next size value at which to resize (capacity * load factor).
	 */
	public int threshold;

	/**
	 * The load factor for the hash table.
	 */
	public final float loadFactor;

	/**
	 * Reference queue for cleared WeakEntries
	 */
	public final ReferenceQueue queue = new ReferenceQueue();

	/**
	 * The number of times this HashMap has been structurally modified
	 * Structural modifications are those that change the number of mappings in
	 * the HashMap or otherwise modify its internal structure (e.g.,
	 * rehash).  This field is used to make iterators on Collection-views of
	 * the HashMap fail-fast.  (See ConcurrentModificationException).
	 */
	public volatile int modCount;

	/**
	 * Constructs a new, empty GroovyWeakIdentityHashMap with the
	 * given initial capacity and the given load factor.
	 *
	 * @param  initialCapacity The initial capacity of the
	 *      GroovyWeakIdentityHashMap
	 * @param  loadFactor      The load factor of the
	 *      GroovyWeakIdentityHashMap
	 * @throws IllegalArgumentException  If the initial capacity is negative,
	 *      or if the load factor is nonpositive.
	 */
	public GroovyWeakIdentityHashMap(int initialCapacity, float loadFactor) {
		if (initialCapacity < 0)
			throw new IllegalArgumentException("Illegal Initial Capacity: "+
					initialCapacity);
		if (initialCapacity > MAXIMUM_CAPACITY)
			initialCapacity = MAXIMUM_CAPACITY;

		if (loadFactor <= 0 || Float.isNaN(loadFactor))
			throw new IllegalArgumentException("Illegal Load factor: "+
					loadFactor);
		int capacity = 1;
		while (capacity < initialCapacity)
			capacity <<= 1;
		@SuppressWarnings("unchecked")
		MyEntry[] tmpTable = (MyEntry[]) new MyEntry[capacity];
		table = tmpTable;
		this.loadFactor = loadFactor;
		threshold = (int)(capacity * loadFactor);
	}

	/**
	 * Constructs a new, empty GroovyWeakIdentityHashMap with the
	 * given initial capacity and the default load factor, which is
	 * 0.75.
	 *
	 * @param  initialCapacity The initial capacity of the
	 *      GroovyWeakIdentityHashMap
	 * @throws IllegalArgumentException  If the initial capacity is negative.
	 */
	public GroovyWeakIdentityHashMap(int initialCapacity) {
		this(initialCapacity, DEFAULT_LOAD_FACTOR);
	}

	/**
	 * Constructs a new, empty GroovyWeakIdentityHashMap with the
	 * default initial capacity (16) and the default load factor
	 * (0.75).
	 */
	public GroovyWeakIdentityHashMap() {
		this.loadFactor = DEFAULT_LOAD_FACTOR;
		threshold = DEFAULT_INITIAL_CAPACITY;
		@SuppressWarnings("unchecked")
		MyEntry[] tmpTable = (MyEntry[]) new MyEntry[DEFAULT_INITIAL_CAPACITY];
		table = tmpTable;
	}

	/**
	 * Constructs a new GroovyWeakIdentityHashMap with the same
	 * mappings as the specified Map.  The
	 * GroovyWeakIdentityHashMap is created with default load
	 * factor, which is 0.75 and an initial capacity
	 * sufficient to hold the mappings in the specified Map.
	 *
	 * @param   t the map whose mappings are to be placed in this map.
	 * @throws  NullPointerException if the specified map is null.
	 * @since   1.3
	 */
	public GroovyWeakIdentityHashMap(Map t) {
		this(Math.max((int) (t.size() / DEFAULT_LOAD_FACTOR) + 1, 16),
				DEFAULT_LOAD_FACTOR);
		putAll(t);
	}

	// internal utilities

	/**
	 * Value representing null keys inside tables.
	 */
	// This is problematic because it isn't of the right type.
	// We can't lie here to the type system by claiming it is of type Object,
	// because NULL_KEY is a static field but Object is a per-instance type parameter.
	public static final Object NULL_KEY = new Object();

	/**
	 * Use NULL_KEY for key if it is null.
	 */
	// not: "public static  Object maskNull(Object key)" because NULL_KEY isn't of type Object.
	public static /*@NonNull*/ Object maskNull(/*@Nullable*/ Object key) {
		return (key.is(null) ? NULL_KEY : key);
	}

	/**
	 * Return internal representation of null key back to caller as null
	 */
	// Argument is actually either of type Object, or is NULL_KEY.
	public static  /*@Nullable*/ Object unmaskNull(Object key) {
		return (key.is(NULL_KEY) ? null : key);
	}

	/**
	 * Check for equality of non-null reference x and possibly-null y.  Uses
	 * identity equality.
	 */
	static boolean eq(Object x, /*@Nullable*/ Object y) {
		return x.is(y);
	}

	/** Return the hash code for x **/
	static int hasher (Object x) {
		return System.identityHashCode (x);
	}

	/**
	 * Return index for hash code h.
	 */
	static int indexFor(int h, int length) {
		return h & (length-1);
	}

	/**
	 * Expunge stale entries from the table.
	 */
	public void expungeStaleEntries() {
		MyEntry e;
		// These types look wrong to me.
		while ( (e = (MyEntry) queue.poll()) != null) { // unchecked cast
			int h = e.hash;
			int i = indexFor(h, table.length);

			MyEntry prev = table[i];
			MyEntry p = prev;
			while (p != null) {
				MyEntry next = p.next;
				if (p.is(e)) {
					if (prev.is(e))
						table[i] = next;
					else
						prev.next = next;
					e.next = null;  // Help GC
					e.value = null; //  "   "
					size--;
					break;
				}
				prev = p;
				p = next;
			}
		}
	}

	/**
	 * Return the table after first expunging stale entries
	 */
	public /*@Nullable*/ MyEntry[] getTable() {
		expungeStaleEntries();
		return table;
	}

	/**
	 * Returns the number of key-value mappings in this map.
	 * This result is a snapshot, and may not reflect unprocessed
	 * entries that will be removed before next attempted access
	 * because they are no longer referenced.
	 */
	public int size() {
		if (size == 0)
			return 0;
		expungeStaleEntries();
		return size;
	}

	/**
	 * Returns true if this map contains no key-value mappings.
	 * This result is a snapshot, and may not reflect unprocessed
	 * entries that will be removed before next attempted access
	 * because they are no longer referenced.
	 */
	public boolean isEmpty() {
		return size() == 0;
	}

	/**
	 * Returns the value to which the specified key is mapped in this weak
	 * hash map, or null if the map contains no mapping for
	 * this key.  A return value of null does not necessarily
	 * indicate that the map contains no mapping for the key; it is also
	 * possible that the map explicitly maps the key to null. The
	 * containsKey method may be used to distinguish these two
	 * cases.
	 *
	 * @param   key the key whose associated value is to be returned.
	 * @return  the value to which this map maps the specified key, or
	 *          null if the map contains no mapping for this key.
	 * @see #put(Object, Object)
	 */
	public /*@Nullable*/ Object get(/*@Nullable*/ Object key) {
		Object k = maskNull(key);
		int h = hasher (k);
		/*@Nullable*/ MyEntry[] tab = getTable();
		int index = indexFor(h, tab.length);
		MyEntry e = tab[index];
		while (e != null) {
			if (e.hash == h && eq(k, e.get()))
				return e.value;
			e = e.next;
		}
		return null;
	}

	/**
	 * Returns true if this map contains a mapping for the
	 * specified key.
	 *
	 * @param   key   The key whose presence in this map is to be tested
	 * @return  true if there is a mapping for key;
	 *          false otherwise
	 */
	public boolean containsKey(/*@Nullable*/ Object key) {
		return getEntry(key) != null;
	}

	/**
	 * Returns the entry associated with the specified key in the HashMap.
	 * Returns null if the HashMap contains no mapping for this key.
	 */
	/*@Nullable*/ MyEntry getEntry(/*@Nullable*/ Object key) {
		Object k = maskNull(key);
		int h = hasher (k);
		/*@Nullable*/ MyEntry[] tab = getTable();
		int index = indexFor(h, tab.length);
		MyEntry e = tab[index];
		while (e != null && !(e.hash == h && eq(k, e.get())))
			e = e.next;
		return e;
	}

	/**
	 * Associates the specified value with the specified key in this map.
	 * If the map previously contained a mapping for this key, the old
	 * value is replaced.
	 *
	 * @param key key with which the specified value is to be associated.
	 * @param value value to be associated with the specified key.
	 * @return previous value associated with specified key, or null
	 *         if there was no mapping for key.  A null return can
	 *         also indicate that the HashMap previously associated
	 *         null with the specified key.
	 */
	public /*@Nullable*/ Object put(Object key, Object value) {
		@SuppressWarnings("unchecked")
		Object k = maskNull(key);
		int h = System.identityHashCode (k);
		/*@Nullable*/ MyEntry[] tab = getTable();
		int i = indexFor(h, tab.length);

		for (MyEntry e = tab[i]; e != null; e = e.next) {
			if (h == e.hash && eq(k, e.get())) {
				Object oldValue = e.value;
				if (value != oldValue)
					e.value = value;
				return oldValue;
			}
		}

		modCount++;
		MyEntry e = tab[i];
		tab[i] = new MyEntry(k, value, queue, h, e);
		if (++size >= threshold)
			resize(tab.length * 2);
		return null;
	}

	/**
	 * Rehashes the contents of this map into a new array with a
	 * larger capacity.  This method is called automatically when the
	 * number of keys in this map reaches its threshold.
	 *
	 * If current capacity is MAXIMUM_CAPACITY, this method does not
	 * resize the map, but sets threshold to Integer.MAX_VALUE.
	 * This has the effect of preventing future calls.
	 *
	 * @param newCapacity the new capacity, MUST be a power of two;
	 *        must be greater than current capacity unless current
	 *        capacity is MAXIMUM_CAPACITY (in which case value
	 *        is irrelevant).
	 */
	void resize(int newCapacity) {
		/*@Nullable*/ MyEntry[] oldTable = getTable();
		int oldCapacity = oldTable.length;
		if (oldCapacity == MAXIMUM_CAPACITY) {
			threshold = Integer.MAX_VALUE;
			return;
		}

		@SuppressWarnings("unchecked")
		MyEntry[] newTable = (MyEntry[]) new MyEntry[newCapacity];
		transfer(oldTable, newTable);
		table = newTable;

		/*
		 * If ignoring null elements and processing ref queue caused massive
		 * shrinkage, then restore old table.  This should be rare, but avoids
		 * unbounded expansion of garbage-filled tables.
		 */
		if (size >= threshold / 2) {
			threshold = (int)(newCapacity * loadFactor);
		} else {
			expungeStaleEntries();
			transfer(newTable, oldTable);
			table = oldTable;
		}
	}

	/** Transfer all entries from src to dest tables */
	public void transfer(/*@Nullable*/ MyEntry[] src, /*@Nullable*/ MyEntry[] dest) {
		for (int j = 0; j < src.length; ++j) {
			MyEntry e = src[j];
			src[j] = null;          // Help GC (?)
			while (e != null) {
				MyEntry next = e.next;
				Object key = e.get();
				if (key == null) {
					e.next = null;  // Help GC
					e.value = null; //  "   "
					size--;
				} else {
					int i = indexFor(e.hash, dest.length);
					e.next = dest[i];
					dest[i] = e;
				}
				e = next;
			}
		}
	}

	/**
	 * Copies all of the mappings from the specified map to this map These
	 * mappings will replace any mappings that this map had for any of the
	 * keys currently in the specified map.
	 *
	 * @param m mappings to be stored in this map.
	 * @throws  NullPointerException if the specified map is null.
	 */
	public void putAll(Map m) {
		int numKeysToBeAdded = m.size();
		if (numKeysToBeAdded == 0)
			return;

		/*
		 * Expand the map if the map if the number of mappings to be added
		 * is greater than or equal to threshold.  This is conservative; the
		 * obvious condition is (m.size() + size) >= threshold, but this
		 * condition could result in a map with twice the appropriate capacity,
		 * if the keys to be added overlap with the keys already in this map.
		 * By using the conservative calculation, we subject ourself
		 * to at most one extra resize.
		 */
		if (numKeysToBeAdded > threshold) {
			int targetCapacity = (int)(numKeysToBeAdded / loadFactor + 1);
			if (targetCapacity > MAXIMUM_CAPACITY)
				targetCapacity = MAXIMUM_CAPACITY;
			int newCapacity = table.length;
			while (newCapacity < targetCapacity)
				newCapacity <<= 1;
			if (newCapacity > table.length)
				resize(newCapacity);
		}

		for (Iterator i = m.entrySet().iterator(); i.hasNext(); ) {
			Map.Entry e = i.next();
			put(e.getKey(), e.getValue());
		}
	}

	/**
	 * Removes the mapping for this key from this map if present.
	 *
	 * @param key key whose mapping is to be removed from the map.
	 * @return previous value associated with specified key, or null
	 *         if there was no mapping for key.  A null return can
	 *         also indicate that the map previously associated null
	 *         with the specified key.
	 */
	public /*@Nullable*/ Object remove(Object key) {
		Object k = maskNull(key);
		int h = hasher (k);
		/*@Nullable*/ MyEntry[] tab = getTable();
		int i = indexFor(h, tab.length);
		MyEntry prev = tab[i];
		MyEntry e = prev;

		while (e != null) {
			MyEntry next = e.next;
			if (h == e.hash && eq(k, e.get())) {
				modCount++;
				size--;
				if (prev.is(e))
					tab[i] = next;
				else
					prev.next = next;
				return e.value;
			}
			prev = e;
			e = next;
		}

		return null;
	}



	/** Special version of remove needed by MyEntry set */
	/*@Nullable*/ MyEntry removeMapping(/*@Nullable*/ Object o) {
		if (!(o instanceof Map.Entry))
			return null;
		/*@Nullable*/ MyEntry[] tab = getTable();
		Map.Entry entry = (/*@NonNull*/ Map.Entry)o;
		Object k = maskNull(entry.getKey());
		int h = hasher (k);
		int i = indexFor(h, tab.length);
		MyEntry prev = tab[i];
		MyEntry e = prev;

		while (e != null) {
			MyEntry next = e.next;
			if (h == e.hash && e.equals(entry)) {
				modCount++;
				size--;
				if (prev.is(e))
					tab[i] = next;
				else
					prev.next = next;
				return e;
			}
			prev = e;
			e = next;
		}

		return null;
	}

	/**
	 * Removes all mappings from this map.
	 */
	public void clear() {
		// clear out ref queue. We don't need to expunge entries
		// since table is getting cleared.
		while (queue.poll() != null)
			;

		modCount++;
		/*@Nullable*/ MyEntry[] tab = table;
		for (int i = 0; i < tab.length; ++i)
			tab[i] = null;                   // Help GC (?)
		size = 0;

		// Allocation of array may have caused GC, which may have caused
		// additional entries to go stale.  Removing these entries from the
		// reference queue will make them eligible for reclamation.
		while (queue.poll() != null)
			;
	}

	/**
	 * Returns true if this map maps one or more keys to the
	 * specified value.
	 *
	 * @param value value whose presence in this map is to be tested.
	 * @return true if this map maps one or more keys to the
	 *         specified value.
	 */
	public boolean containsValue(/*@Nullable*/ Object value) {
		if (value==null)
			return containsNullValue();

		/*@Nullable*/ MyEntry[] tab = getTable();
		for (int i = tab.length ; i-- > 0 ;)
			for (MyEntry e = tab[i] ; e != null ; e = e.next)
				if (value.equals(e.value))
					return true;
		return false;
	}

	/**
	 * Special-case code for containsValue with null argument
	 */
	public boolean containsNullValue() {
		/*@Nullable*/ MyEntry[] tab = getTable();
		for (int i = tab.length ; i-- > 0 ;)
			for (MyEntry e = tab[i] ; e != null ; e = e.next)
				if (e.value==null)
					return true;
		return false;
	}

	// Views

	public transient /*@Nullable*/ Set entrySet = null;
	public transient volatile /*@Nullable*/ Set   our_keySet = null;

	/**
	 * Returns a set view of the keys contained in this map.  The set is
	 * backed by the map, so changes to the map are reflected in the set, and
	 * vice-versa.  The set supports element removal, which removes the
	 * corresponding mapping from this map, via the Iterator.remove,
	 * Set.remove, removeAll, retainAll, and
	 * clear operations.  It does not support the add or
	 * addAll operations.
	 *
	 * @return a set view of the keys contained in this map.
	 */
	public Set keySet() {
		Set ks = our_keySet;
		return (ks != null ? ks : (our_keySet = new KeySet()));
	}
	
	transient volatile /*@Nullable*/ Collection our_values = null;

	/**
	 * Returns a collection view of the values contained in this map.  The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa.  The collection supports element
	 * removal, which removes the corresponding mapping from this map, via the
	 * Iterator.remove, Collection.remove,
	 * removeAll, retainAll, and clear operations.
	 * It does not support the add or addAll operations.
	 *
	 * @return a collection view of the values contained in this map.
	 */
	public Collection values() {
		Collection vs = our_values;
		return (vs != null ?  vs : (our_values = new Values()));
	}

	/**
	 * Returns a collection view of the mappings contained in this map.  Each
	 * element in the returned collection is a Map.Entry.  The
	 * collection is backed by the map, so changes to the map are reflected in
	 * the collection, and vice-versa.  The collection supports element
	 * removal, which removes the corresponding mapping from the map, via the
	 * Iterator.remove, Collection.remove,
	 * removeAll, retainAll, and clear operations.
	 * It does not support the add or addAll operations.
	 *
	 * @return a collection view of the mappings contained in this map.
	 * @see java.util.Map.Entry
	 */
	public Set entrySet() {
		Set es = entrySet;
		return (es != null ? es : (entrySet = new MyEntrySet()));
	}
}

public class MyEntrySet extends AbstractSet {
	public GroovyWeakIdentityHashMap map;
	
	public MyEntrySet(GroovyWeakIdentityHashMap map){
		this.map = map;
	}
	
	public Iterator iterator() {
		return new HashIterator(map){
			public Object next(){
				return nextEntry();
			}
		};
	}
	
	public boolean contains(/*@Nullable*/ Object o) {
		if (!(o instanceof Map.Entry))
			return false;
		Map.Entry e = (/*@NonNull*/ Map.Entry)o;
		Object k = e.getKey();
		MyEntry candidate = map.getEntry(e.getKey());
		return candidate != null && candidate.equals(e);
	}
	
	public boolean remove(/*@Nullable*/ Object o) {
		return map.removeMapping(o) != null;
	}
	
	public int size() {
		return map.size();
	}
	
	public void clear() {
		map.clear();
	}
	
	public Object[] toArray() {
		Collection c = new ArrayList(size());
		for (Iterator i = iterator(); i.hasNext(); )
			c.add(new OurSimpleEntry(i.next()));
		return c.toArray();
	}
	
	public Object[] toArray(Object[] a) {
		Collection c = new ArrayList(size());
		for (Iterator i = iterator(); i.hasNext(); )
			c.add(new OurSimpleEntry(i.next()));
		return c.toArray(a);
	}
}

/** Version copied from Abstract Map because it is not public **/
public class OurSimpleEntry implements Map.Entry {
	Object key;
	Object value;
	
	public OurSimpleEntry(Object key, Object value) {
		this.key   = key;
		this.value = value;
	}
	
	public OurSimpleEntry(Map.Entry e) {
		this.key   = e.getKey();
		this.value = e.getValue();
	}
	
	public Object getKey() {
		return key;
	}
	
	public Object getValue() {
		return value;
	}
	
	public Object setValue(Object value) {
		Object oldValue = this.value;
		this.value = value;
		return oldValue;
	}
	
	public boolean equals(/*@Nullable*/ Object o) {
		if (!(o instanceof Map.Entry))
			return false;
		Map.Entry e = (Map.Entry)o;
		return GroovyWeakIdentityHashMap.eq(key, e.getKey()) && eq(value, e.getValue());
	}
	
	public int hashCode() {
		return ((key == null)   ? 0 :   key.hashCode()) ^
		((value == null)   ? 0 : value.hashCode());
	}
	
	public String toString() {
		return key.toString() + "=" + value.toString();
	}
	
	public static boolean eq(/*@Nullable*/ Object o1, /*@Nullable*/ Object o2) {
		return (o1 == null ? o2 == null : o1.equals(o2));
	}
}

public class KeySet extends AbstractSet {
	public GroovyWeakIdentityHashMap map;
	
	public KeySet(GroovyWeakIdentityHashMap map){
		this.map=map;
	}
	
	public Iterator iterator() {
		return new HashIterator(map){
			public Object next(){
				return nextEntry().getKey();
			}
		};
	}
	
	public int size() {
		return map.size();
	}
	
	public boolean contains(/*@Nullable*/ Object o) {
		return map.containsKey(o);
	}
	
	public boolean remove(/*@Nullable*/ Object o) {
		if (map.containsKey(o)) {
			map.remove(o);
			return true;
		}
		else
			return false;
	}
	
	public void clear() {
		map.clear();
	}
	
	public Object[] toArray() {
		Collection c = new ArrayList(size());
		for (Iterator i = iterator(); i.hasNext(); )
			c.add(i.next());
		return c.toArray();
	}
	
	public Object[] toArray(Object[] a) {
		Collection c = new ArrayList(size());
		for (Iterator i = iterator(); i.hasNext(); )
			c.add(i.next());
		return c.toArray(a);
	}
}

public class Values extends AbstractCollection {
	public GroovyWeakIdentityHashMap map;
	
	public Values(GroovyWeakIdentityHashMap map){
		this.map=map;
	}
	
	public Iterator iterator() {
		return new HashIterator(map){
			public Object next(){
				return nextEntry().getValue();
			}
		};
	}
	
	public int size() {
		return map.size();
	}
	
	public boolean contains(/*@Nullable*/ Object o) {
		return map.containsValue(o);
	}
	
	public void clear() {
		map.clear();
	}
	
	public Object[] toArray() {
		Collection c = new ArrayList(map.size());
		for (Iterator i = iterator(); i.hasNext(); )
			c.add(i.next());
		return c.toArray();
	}
	
	public Object[] toArray(Object[] a) {
		Collection c = new ArrayList(map.size());
		for (Iterator i = iterator(); i.hasNext(); )
			c.add(i.next());
		return c.toArray(a);
	}
}
/**
 * The entries in this hash table extend WeakReference, using its main ref
 * field as the key.
 */
public class MyEntry extends WeakReference implements Map.Entry {
	public Object value;
	public final int hash;
	public /*@Nullable*/ MyEntry next;
	
	/**
	 * Create new entry.
	 */
	MyEntry(Object key, Object value,
	ReferenceQueue queue,
	int hash, MyEntry next) {
		super(key, queue);
		this.value = value;
		this.hash  = hash;
		this.next  = next;
	}
	
	public Object getKey() {
		return GroovyWeakIdentityHashMap.unmaskNull(get());
	}
	
	public Object getValue() {
		return value;
	}
	
	public Object setValue(Object newValue) {
		Object oldValue = value;
		value = newValue;
		return oldValue;
	}
	
	public boolean equals(/*@Nullable*/ Object o) {
		if (!(o instanceof Map.Entry))
			return false;
		Map.Entry e = (/*@NonNull*/ Map.Entry)o;   // This annotation shouldn't be necessary??
		Object k1 = getKey();
		Object k2 = e.getKey();
		if (GroovyWeakIdentityHashMap.eq (k1, k2)) {
			Object v1 = getValue();
			Object v2 = e.getValue();
			if (v1.is(v2) || (v1 != null && v1.equals(v2)))
				return true;
		}
		return false;
	}
	
	public int hashCode() {
		Object k = getKey();
		Object v = getValue();
		return  ((k==null ? 0 : GroovyWeakIdentityHashMap.hasher (k)) ^
		(v==null ? 0 : v.hashCode()));
	}
	
	public String toString() {
		return getKey().toString() + "=" + getValue().toString();
	}
}

public abstract class HashIterator implements Iterator {
	public int index;
	/*@Nullable*/ public MyEntry entry = null;
	/*@Nullable*/ public MyEntry lastReturned = null;
	public int expectedModCount;
	
	GroovyWeakIdentityHashMap map;
	
	/**
	 * Strong reference needed to avoid disappearance of key
	 * between hasNext and next
	 */
	/*@Nullable*/ Object nextKey = null;
	
	/**
	 * Strong reference needed to avoid disappearance of key
	 * between nextEntry() and any use of the entry
	 */
	/*@Nullable*/ Object currentKey = null;
	
	HashIterator(GroovyWeakIdentityHashMap map) {
		this.map=map;
		expectedModCount = map.modCount;
		index = (map.size() != 0 ? map.table.length : 0);
	}
	
	public boolean hasNext() {
		/*@Nullable*/ MyEntry[] t = map.table;
		
		while (nextKey == null) {
			MyEntry e = entry;
			int i = index;
			while (e == null && i > 0)
				e = t[--i];
			entry = e;
			index = i;
			if (e == null) {
				currentKey = null;
				return false;
			}
			nextKey = e.get(); // hold on to key in strong ref
			if (nextKey == null)
				entry = entry.next;
		}
		return true;
	}
	
	/** The common parts of next() across different types of iterators */
	public MyEntry nextEntry() {
		if (map.modCount != expectedModCount)
			throw new ConcurrentModificationException();
		if (nextKey == null && !hasNext())
			throw new NoSuchElementException();
		
		lastReturned = entry;
		entry = entry.next;
		currentKey = nextKey;
		nextKey = null;
		return lastReturned;
	}
	
	public void remove() {
		if (lastReturned == null)
			throw new IllegalStateException();
		if (map.modCount != expectedModCount)
			throw new ConcurrentModificationException();
		
		map.remove(currentKey);
		expectedModCount = map.modCount;
		lastReturned = null;
		currentKey = null;
	}
}
