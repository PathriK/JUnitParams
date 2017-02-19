package junitparams;


import org.junit.Test;
import org.junit.runner.RunWith;
import static org.assertj.core.api.Assertions.*;

import junitparams.usage.person_example.PersonType;

@RunWith(JUnitParamsRunner.class)
public class EnumsAsParamsTest2 {

    @Test
    @Parameters({"SOME_VALUE", "OTHER_VALUE"})
    public void passEnumAsString(PersonType person) {
        assertThat(person).isIn(PersonType.SOME_VALUE, PersonType.OTHER_VALUE);
    }    
}
