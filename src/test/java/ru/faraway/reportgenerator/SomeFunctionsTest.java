package ru.faraway.reportgenerator;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.Assert;
import org.junit.Test;
import ru.faraway.reportgenerator.utils.PatternUtils;

/**
 * Created by FarAway on 13.05.2016.
 *
 * Tests for line-wrapping
 */
public class SomeFunctionsTest {

    @Test
    public void testGetCutIndex(){
        Pair<String, String> parts = cuttedParts("12.12.12", 7);
        Assert.assertEquals("12.12.", parts.getLeft());
        Assert.assertEquals("12", parts.getRight());

        parts = cuttedParts("Солодов ", 7);
        Assert.assertEquals("Солодов", parts.getLeft());
        Assert.assertEquals("", parts.getRight());

        parts = cuttedParts("Солодов", 6);
        Assert.assertEquals("Солодо", parts.getLeft());
        Assert.assertEquals("в", parts.getRight());

        parts = cuttedParts("Солодов-Распутин-Белялетдинов", 16);
        Assert.assertEquals("Солодов-Распутин", parts.getLeft());
        Assert.assertEquals("-Белялетдинов", parts.getRight());
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCutIndexWithIllegalArgs1() {
        getCutIndex("Солодов", 7);
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetCutIndexWithIllegalArgs2() {
        Pair<String, String> parts4 = cuttedParts("", 6);
    }

    public Pair<String, String> cuttedParts(String data, int width) {
        int cutIndex = getCutIndex(data, width);
        String left = data.substring(0, cutIndex);
        String right = data.substring(cutIndex).trim();
        return new ImmutablePair<>(left, right);
    }

    // Copy of private function in class ReportWriter for tests
    private int getCutIndex(String data, int width) {
        if (data.length() <= width) {
            throw new IllegalArgumentException("Nothing to cut. There is enough width for data");
        }

        String firstPart = data.substring(0, width);
        boolean wordCutted = data.substring(width - 1, width + 1).matches(PatternUtils.unicode_charclass("\\w{2}"));
        if (!wordCutted) return width;
        else {
            String s = firstPart.replaceAll(PatternUtils.unicode_charclass("\\W")," ");
            int separator = s.lastIndexOf(" ");
            return separator != -1 ? separator + 1 : width;
        }
    }
}