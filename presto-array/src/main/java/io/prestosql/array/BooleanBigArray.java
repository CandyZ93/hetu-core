/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.prestosql.array;

import io.airlift.slice.SizeOf;
import org.openjdk.jol.info.ClassLayout;

import java.util.Arrays;

import static io.airlift.slice.SizeOf.sizeOfBooleanArray;

// Note: this code was forked from fastutil (http://fastutil.di.unimi.it/)
// Copyright (C) 2010-2013 Sebastiano Vigna
public final class BooleanBigArray
{
    private static final int INSTANCE_SIZE = ClassLayout.parseClass(BooleanBigArray.class).instanceSize();
    private static final long SIZE_OF_SEGMENT = sizeOfBooleanArray(BigArrays.SEGMENT_SIZE);

    private final boolean initialValue;

    private boolean[][] array;
    private int capacity;
    private int segments;

    /**
     * Creates a new big array containing one initial segment
     */
    public BooleanBigArray()
    {
        this(false);
    }

    public BooleanBigArray(boolean initialValue)
    {
        this.initialValue = initialValue;
        array = new boolean[BigArrays.INITIAL_SEGMENTS][];
        allocateNewSegment();
    }

    /**
     * Returns the size of this big array in bytes.
     */
    public long sizeOf()
    {
        return INSTANCE_SIZE + SizeOf.sizeOf(array) + (segments * SIZE_OF_SEGMENT);
    }

    /**
     * Returns the element of this big array at specified index.
     *
     * @param index a position in this big array.
     * @return the element of this big array at the specified position.
     */
    public boolean get(long index)
    {
        return array[BigArrays.segment(index)][BigArrays.offset(index)];
    }

    /**
     * Sets the element of this big array at specified index.
     *
     * @param index a position in this big array.
     */
    public void set(long index, boolean value)
    {
        array[BigArrays.segment(index)][BigArrays.offset(index)] = value;
    }

    /**
     * Ensures this big array is at least the specified length.  If the array is smaller, segments
     * are added until the array is larger then the specified length.
     */
    public void ensureCapacity(long length)
    {
        if (capacity > length) {
            return;
        }

        grow(length);
    }

    private void grow(long length)
    {
        // how many segments are required to get to the length?
        int requiredSegments = BigArrays.segment(length) + 1;

        // grow base array if necessary
        if (array.length < requiredSegments) {
            array = Arrays.copyOf(array, requiredSegments);
        }

        // add new segments
        while (segments < requiredSegments) {
            allocateNewSegment();
        }
    }

    private void allocateNewSegment()
    {
        boolean[] newSegment = new boolean[BigArrays.SEGMENT_SIZE];
        if (initialValue) {
            Arrays.fill(newSegment, initialValue);
        }
        array[segments] = newSegment;
        capacity += BigArrays.SEGMENT_SIZE;
        segments++;
    }
}
