package com.github.mehdihadeli.buildingblocks.test;

import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

// - `@ExtendWith(MockitoExtension.class)` is a JUnit 5 (Jupiter) feature that integrates Mockito with your JUnit tests.
// It simplifies the process of creating and injecting mock objects into your test class.
// - It automatically initializes fields annotated with @Mock as Mockito mocks.
// - It automatically injects the mock objects into fields annotated with @InjectMocks.
@ExtendWith(MockitoExtension.class)
public abstract class UnitTestBase {
}
