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
package io.prestosql.type;

import io.airlift.slice.Slice;
import io.airlift.slice.Slices;
import io.prestosql.spi.block.Block;
import io.prestosql.spi.block.BlockBuilder;
import io.prestosql.spi.type.Type;
import io.prestosql.spi.type.VarcharType;
import org.testng.annotations.Test;

import static io.prestosql.spi.type.VarcharType.VARCHAR;
import static io.prestosql.spi.type.VarcharType.createVarcharType;
import static org.testng.Assert.assertEquals;

public class TestVarcharType
        extends AbstractTestType
{
    public TestVarcharType()
    {
        super(VARCHAR, String.class, createTestBlock());
    }

    public static Block createTestBlock()
    {
        BlockBuilder blockBuilder = VARCHAR.createBlockBuilder(null, 15);
        VARCHAR.writeString(blockBuilder, "apple");
        VARCHAR.writeString(blockBuilder, "apple");
        VARCHAR.writeString(blockBuilder, "apple");
        VARCHAR.writeString(blockBuilder, "banana");
        VARCHAR.writeString(blockBuilder, "banana");
        VARCHAR.writeString(blockBuilder, "banana");
        VARCHAR.writeString(blockBuilder, "banana");
        VARCHAR.writeString(blockBuilder, "banana");
        VARCHAR.writeString(blockBuilder, "cherry");
        VARCHAR.writeString(blockBuilder, "cherry");
        VARCHAR.writeString(blockBuilder, "date");
        return blockBuilder.build();
    }

    @Test
    public void testValidateData()
    {
        final Block testBlock = createTestBlock();
        assertEquals("apple", VARCHAR.getObjectValue(null, testBlock, 0));
        assertEquals("apple", VARCHAR.getObjectValue(null, testBlock, 1));
        assertEquals("apple", VARCHAR.getObjectValue(null, testBlock, 2));
        assertEquals("banana", VARCHAR.getObjectValue(null, testBlock, 3));
        assertEquals("banana", VARCHAR.getObjectValue(null, testBlock, 4));
        assertEquals("banana", VARCHAR.getObjectValue(null, testBlock, 5));
        assertEquals("banana", VARCHAR.getObjectValue(null, testBlock, 6));
        assertEquals("banana", VARCHAR.getObjectValue(null, testBlock, 7));
        assertEquals("cherry", VARCHAR.getObjectValue(null, testBlock, 8));
        assertEquals("cherry", VARCHAR.getObjectValue(null, testBlock, 9));
        assertEquals("date", VARCHAR.getObjectValue(null, testBlock, 10));
    }

    @Override
    protected Object getGreaterValue(Object value)
    {
        return Slices.utf8Slice(((Slice) value).toStringUtf8() + "_");
    }

    @Test
    public void testRange()
    {
        VarcharType type = createVarcharType(5);

        Type.Range range = type.getRange().get();

        String expectedMax = new StringBuilder()
                .appendCodePoint(Character.MAX_CODE_POINT)
                .appendCodePoint(Character.MAX_CODE_POINT)
                .appendCodePoint(Character.MAX_CODE_POINT)
                .appendCodePoint(Character.MAX_CODE_POINT)
                .appendCodePoint(Character.MAX_CODE_POINT)
                .toString();

        assertEquals(Slices.utf8Slice(""), range.getMin());
        assertEquals(Slices.utf8Slice(expectedMax), range.getMax());
    }
}
