package junitparams;

import static org.junit.Assert.*;

import org.junit.*;
import org.junit.runner.*;

import junitparams.usage.person_example.*;

@RunWith(JUnitParamsRunner.class)
public class EnumsAsParamsTest2 {

    @Test
    @Parameters({"SOME_VALUE", "OTHER_VALUE"})
    public void passEnumAsString(PersonType person) {
        //assertThat(person).isIn(PersonType.SOME_VALUE, PersonType.OTHER_VALUE);
    	assertTrue(false);
    }    
}
