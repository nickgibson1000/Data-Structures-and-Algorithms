import java.util.ArrayList;
import java.util.Collections;

import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class UnorderedMap<KEY, T> {
    final int INIT_SIZE = 101;

    class Entry{
		KEY key;
		T data;
		Entry next;

        Entry() {
            next = null;
        }

		Entry(KEY newKey, T newData, Entry newNext) {
            key = newKey;
            data = newData;
            next = newNext;
        }
	};

	private ArrayList<Entry> table; // each table index has a linked list of Entries.
	private int tablesize;  // table size
	private int entrysize;  // total number of entries

    // Internal method to test if a positive number is prime (not efficient)
    private boolean isPrime( int n ){
        if( n == 2 || n == 3 )
            return true;
        if( n == 1 || n % 2 == 0 )
            return false;
        for( int i = 3; i * i <= n; i += 2 )
            if( n % i == 0 )
                return false;
        return true;
    }

    // Internal method to return a prime number at least as large as n.
    // Assumes n > 0.
    private int nextPrime( int n ){
        if( n % 2 == 0 )
            n++;
        for( ; !isPrime( n ); n += 2 )
            ;
        return n;
    }   

    // Looks Good
    // Constructs an empty unordered_map object, containing no elements and with a entry size of zero
    // Notice the hash table with a default size 101 is constructed but empty.
	public UnorderedMap() {

        this.tablesize = INIT_SIZE;
        this.entrysize = 0;
        this.table = new ArrayList<>(tablesize);

        // Add null values as placeholders for table because arraylist is weird
        for(int i = 0; i <= this.tablesize; i++) {
            table.add(null);
        }
    }

	// Copy constructor The object is initialized to have the same contents and properties as the UnorderedMap object rhs.
	UnorderedMap(UnorderedMap<KEY, T> rhs) {
        
        // SHALLOW COPY CHANGE TO MAKE A DEEP COPY
        this.table = rhs.table;
        this.entrysize = rhs.entrysize;
        this.tablesize = rhs.tablesize;
    }

	// A simple forward iterator. The order of iterator is not specified.
	class Iterator{

        private Entry _cur;
        private int bucketIndex;

		public Iterator(){}

		public Iterator(Entry cur) {
            _cur = cur;

            // Generate our hash code for Entry key
            int hashCode = _cur.key.hashCode();
            // Find index for our current entry key
            bucketIndex = Math.floorMod(hashCode, tablesize);
        }

        public boolean hasNext() {
            return (_cur != null);
        }

        // Looks Good
		public Entry next() { 

            // If we have reached the end of the current indexes chain
            // We have to find the next occupied index
            if(_cur.next == null) {

                // Store current entry to return later
                Entry returnEntry = _cur;

                // Advance to the next index
                bucketIndex++;

                // Loop through table to find next occipied index
                while (bucketIndex < table.size()) {

                    // Found a non-empty index
                    if (table.get(bucketIndex) != null) { 

                        // Get first Entry in the chain
                        _cur = table.get(bucketIndex); 

                        // Return the entry we found earlier
                        return returnEntry;
                    }
                    // Continue searching for index
                    bucketIndex++; 
                }

                // No more elements in the table
                _cur = null;
                return returnEntry; // Return the last saved entry
            }
            
            
            //Chain is not empty and we just want to find next Entry in chain
            // Store current entry to return
            Entry returnEntry = _cur;

            // Set _cur to next Entry in chain
            _cur = _cur.next;

            // Return current Entry we found
            return returnEntry;
        }

        // Looks Good
        @Override
		public boolean equals(Object ob) {

            // Check if ob is null or not an instance of this iterator class
            if (this == ob) return true; // Check for reference equality
            if (ob == null || ob.getClass() != this.getClass()) {
                return false;
            }

            // Cast after the above check
            Iterator other = (Iterator) ob;

            // Check if iterators point to the same Entry
            return (this._cur == null && other._cur == null) || 
                (this._cur != null && this._cur.equals(other._cur));
        }
	};

    // Looks Good
	// Returns an iterator at the first element in the UnorderedMap container
    // Note that an UnorderedMap object makes no guarantees on which specific element is considered its first element.
	// But, in any case, the range that goes from its begin to its end covers all the elements in the container.
	Iterator iterator() {

        // Use -1 as marker to see if map is empty
        int firstIndex = -1;
        int i = 0;

        // Go through table finding first element
        while(i < table.size()) {

            if(table.get(i) != null) {
                firstIndex = i;
                break;
            }
            i++;
        }

        // If still -1 that means map is empty so return an empty iterator
        if(firstIndex == -1) {
            return new Iterator();
        }
        
        // Create new iterator with first index containing an item
        Iterator it = new Iterator(table.get(firstIndex));

        // Return new Iterator
        return it;
    }

    // Looks Good
    // applies the action on each entry in the Hash Table
    public void forEach(BiConsumer<? super KEY, ? super T> action) {
        
        // Create iterator
        Iterator it = this.iterator();

        // Iterate thorugh table
        while(it.hasNext()) {

            // Get next entry
            Entry entry = it.next();
            action.accept(entry.key, entry.data);
        }
    }

    // applies the function on each entry in the Hash Table, updated the Entry's
    // data
    public void replaceAll(BiFunction<? super KEY, ? super T, ? extends T> function) {
        
        // Create iterator
        Iterator it = this.iterator();

        // Iterate through table 
        while(it.hasNext()) {

            // Get our next entry
            Entry entry = it.next();
            entry.data = function.apply(entry.key, entry.data);
        }
    }

    //Looks Good
	// Inserts new elements in the unordered_map.
    // Each element is inserted only if its key is not equivalent to the key of any other element already in the container
	// (keys in an UnorderedMap are unique). function returns true if new element is inserted, false otherwise.
	boolean put(KEY key, T val) {

        // Check if the hashmap already contains key
        if(this.containsKey(key)) {
            return false; // Return false if key exists
        }

        
        // Update entry size of table
        this.entrysize++;

        // Get the keys hashcode and find its index to insert
        int hashcode = key.hashCode();
        int bucketIndex = Math.floorMod(hashcode, this.tablesize);

        // Check if bucket index has a value in it yes, if not add new value
        if(table.get(bucketIndex) == null) {

            // Create new entry to add
            Entry newNode = new Entry(key, val, null);
            // Add entry to our hashed index
            table.set(bucketIndex, newNode);
            
        } else {

            // If index is not null find head of the index (linked list)
            Entry head = this.table.get(bucketIndex);

            // Traverse the linked list to the end
            while(head.next != null) { head = head.next; }

            // Create new entry, add key and value and add to end of linked list
            head.next = new Entry(key, val, null);
        }


        // Check load factor and rehash if needed
        if ((double) entrysize / tablesize > 2.0) {
            rehash();
        }

        return true;
    }

    // Looks Good
	// Removes from the UnorderedMap container a single element
	// returns true if the element is erased, false otherwise
	boolean erase (KEY key) {

        // If key is not in map return false
        // If entry size of map is return false
        if(!this.containsKey(key) || this.entrysize == 0) {
            return false;
        }

        // Get the keys hashcode and find its index to insert
        int hashcode = key.hashCode();
        int bucketIndex = Math.floorMod(hashcode, this.tablesize);

        // Get head of chain at key index
        Entry head = table.get(bucketIndex);

        // Case 1: If entry is only entry in the that indexes chain
        if(head.next == null) {

            // Set the index too null removing the entry
            table.set(bucketIndex, null);

        // Case 2: If entry is part of a chain and is the head
        } else if( head.key.equals(key)) {

            // Set head of chain to the next entry effectively removing the entry we want
            table.set(bucketIndex, head.next);

        // Case 3: If entry is simply in chain somewhere
        } else {

            // Create a child of head so that we can keep track of parent (head)
            // This is needed to link parent.next to child.next removing the entry we want
            Entry child = head.next;

            // Find the proper entry key belongs too
            while(!child.key.equals(key)) {

                child = child.next;
                head = head.next;

                // When we find the entry, remove it and restore links
                if(child.key.equals(key)) {
                    head.next = child.next;
                }
            }
        }
        
        // Update entry size
        this.entrysize--;
        // Key successfully removed
        return true;
    }


    // Searches the container for an element with the given key and returns the associated Entry if found,
	// otherwise it returns null.
	private Entry getEntry(KEY key) {

        // Return false if method is called on empty table
        // Or if key does not exist in table
        if(this.entrysize == 0) {
            return null;
        }

        // Get the keys hashcode and find its index to insert
        int hashcode = key.hashCode();
        int bucketIndex = Math.floorMod(hashcode, this.tablesize);

        // If nothing is at keys hashed index, key does not exist
        if(table.get(bucketIndex) == null) {
            return null;
        }

            // Get head of chain
            Entry head = table.get(bucketIndex);

            // Iterate through chain looking for key
            while(head != null) {

                // Key found
                if(head.key.equals(key)) {
                    return head;
                }
                head = head.next;
            }
        
        // Entry contianing key not found
        return null;
    }

    // Looks Good
	// Searches the container for an element with the given key and returns the associated value if found,
	// otherwise it returns null.
	T get(KEY key) {

        // If key does not exist return null
        if(!this.containsKey(key)) {
            return null;
        }

        // Get the entry associated with our key
        Entry entry = getEntry(key);

        // Return data of the entry
        return entry.data;
    }

    // Looks Good
    // Searches the container for an element with the given key and returns true if found,
	// otherwise it returns null.
	boolean containsKey(KEY key) {

        // Return false if method is called on empty table
        if(this.entrysize == 0) {
            return false;
        }

        // Get the keys hashcode and find its index to insert
        int hashcode = key.hashCode();
        int bucketIndex = Math.floorMod(hashcode, this.tablesize);

        // If nothing is at keys hashed index, key does not exist
        if(table.get(bucketIndex) == null) {
            return false;
        }

            // Get head of chain
            Entry head = table.get(bucketIndex);

            // Iterate through chain looking for key
            while(head != null) {

                // Key found
                if(head.key.equals(key)) {
                    return true;
                }
                head = head.next;
            }
    
        
        // Key was not found
        return false;
    }

    // Replaces the value for the specified key only if already mapped to a value. Returns the
    // prior value in that case. If it was not already mapped, returns false.
	T replace(KEY key, T val) {
        
        if(this.table.size() == 0) {
            return null;
        }

        // Get the keys hashcode and find its index to insert
        int hashcode = key.hashCode();
        int bucketIndex = Math.floorMod(hashcode, this.tablesize);

        // If nothing is at keys hashed index, key does not exist
        if(table.get(bucketIndex) == null) {
            return null;
        }

            // Get head of chain
            Entry head = table.get(bucketIndex);

            // Iterate through chain looking for key
            while(head != null) {

                // Key found
                if(head.key.equals(key)) {

                    // Store current value in entry
                    T value = head.data;
                    // Update Entrys value
                    head.data = val;
                    // Return old value
                    return value;
                }
                head = head.next;
            }
        
        // How did we get here?
        return null;
    }

	// Returns the number of elements in the UnorderedMap container.
	int size(){ return entrysize; }

	// Returns the table size; included for verifying rehash operation.
	int tsize(){ return tablesize; }

    // double the hashtable when the load factor reaches 2.
	private void rehash() {

        // Calculate new table size
        int newTableSize = nextPrime(tablesize * 2);

        // Create a new table
        ArrayList<Entry> newTable = new ArrayList<>(Collections.nCopies(newTableSize, null));

        // Reinsert all entries into the new table
        for (int i = 0; i < tablesize; i++) {
            Entry current = table.get(i);

            while (current != null) {
                // Compute the new index for the current entry
                int newBucketIndex = Math.floorMod(current.key.hashCode(), newTableSize);

                // Insert into the new table (handle chaining if necessary)
                if (newTable.get(newBucketIndex) == null) {
                    newTable.set(newBucketIndex, new Entry(current.key, current.data, null));
                } else {
                    Entry head = newTable.get(newBucketIndex);
                    while (head.next != null) {
                        head = head.next;
                    }
                    head.next = new Entry(current.key, current.data, null);
                }

                // Move to the next entry in the chain
                current = current.next;
            }
        }

        // Replace the old table and update the table size
        table = newTable;
        tablesize = newTableSize;
            
    }
}
