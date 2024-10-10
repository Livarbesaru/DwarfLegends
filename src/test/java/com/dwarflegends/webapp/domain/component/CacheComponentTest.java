package com.dwarflegends.webapp.domain.component;

import com.dwarflegends.webapp.domain.component.impl.CacheComponent;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@Import({CacheComponent.class})
@TestPropertySource(properties = {
        "page.tag-path={'main':'main','work':'work','job':'job','about':'about'}"
})
public class CacheComponentTest {
    @Autowired
    private CacheComponent cacheComponent;

    @Test
    void cacheTag() {
        Assertions.assertThat(cacheComponent.getAvailableTag()).isNotNull().isNotEmpty();
    }
}
