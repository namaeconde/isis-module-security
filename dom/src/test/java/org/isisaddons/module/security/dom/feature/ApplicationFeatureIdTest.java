/*
 *  Copyright 2014 Dan Haywood
 *
 *  Licensed under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package org.isisaddons.module.security.dom.feature;

import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import com.danhaywood.java.testsupport.coverage.PrivateConstructorTester;
import com.google.common.base.Function;

import org.jmock.Expectations;
import org.jmock.auto.Mock;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import org.apache.isis.core.unittestsupport.jmocking.JUnitRuleMockery2;
import org.apache.isis.core.unittestsupport.value.ValueTypeContractTestAbstract;

import org.isisaddons.module.security.dom.SerializationContractTest;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.emptyCollectionOf;
import static org.junit.Assert.assertThat;

public class ApplicationFeatureIdTest {

    @Rule
    public JUnitRuleMockery2 context = JUnitRuleMockery2.createFor(JUnitRuleMockery2.Mode.INTERFACES_AND_CLASSES);

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    public static class Title extends ApplicationFeatureIdTest {

        @Test
        public void happyCase() throws Exception {
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar#foo");

            assertThat(applicationFeatureId.title(), is("com.mycompany.Bar#foo"));
        }
    }

    public static class NewPackage extends ApplicationFeatureIdTest {

        @Test
        public void testNewPackage() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany");
            // then
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is(nullValue()));
            assertThat(applicationFeatureId.getMemberName(), is(nullValue()));
        }
    }

    public static class NewClass extends ApplicationFeatureIdTest {

        @Test
        public void testNewClass() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");
            // then
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.CLASS));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is("Bar"));
            assertThat(applicationFeatureId.getMemberName(), is(nullValue()));
        }
    }

    public static class NewMember extends ApplicationFeatureIdTest {

        @Test
        public void using_fullyQualifiedClassName_and_MemberName() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");
            // then
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.MEMBER));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is("Bar"));
            assertThat(applicationFeatureId.getMemberName(), is("foo"));
        }

        @Test
        public void using_fullyQualifiedName() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar#foo");
            // then
            assertThat(applicationFeatureId.getType(), is(ApplicationFeatureType.MEMBER));
            assertThat(applicationFeatureId.getPackageName(), is("com.mycompany"));
            assertThat(applicationFeatureId.getClassName(), is("Bar"));
            assertThat(applicationFeatureId.getMemberName(), is("foo"));
        }

    }

    public static class Constructor_AFT_String extends ApplicationFeatureIdTest {

        @Test
        public void whenPackage() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = new ApplicationFeatureId(ApplicationFeatureType.PACKAGE, "com.mycompany");
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newPackage("com.mycompany")));
        }

        @Test
        public void whenClass() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = new ApplicationFeatureId(ApplicationFeatureType.CLASS, "com.mycompany.Bar");
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newClass("com.mycompany.Bar")));
        }

        @Test
        public void whenMember() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = new ApplicationFeatureId(ApplicationFeatureType.MEMBER, "com.mycompany.Bar#foo");
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newMember("com.mycompany.Bar","foo")));
        }
    }

    public static class NewFeature_AFT_String extends ApplicationFeatureIdTest {

        @Test
        public void whenPackage() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature(ApplicationFeatureType.PACKAGE, "com.mycompany");
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newPackage("com.mycompany")));
        }

        @Test
        public void whenClass() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature(ApplicationFeatureType.CLASS, "com.mycompany.Bar");
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newClass("com.mycompany.Bar")));
        }

        @Test
        public void whenMember() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature(ApplicationFeatureType.MEMBER, "com.mycompany.Bar#foo");
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newMember("com.mycompany.Bar","foo")));
        }
    }

    public static class NewFeature_String_String_String extends ApplicationFeatureIdTest {

        @Test
        public void whenPackage() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature("com.mycompany", null, null);
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newPackage("com.mycompany")));
        }

        @Test
        public void whenClass() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature("com.mycompany", "Bar", null);
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newClass("com.mycompany.Bar")));
        }

        @Test
        public void whenMember() throws Exception {
            // when
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newFeature("com.mycompany", "Bar", "foo");
            // then
            assertThat(applicationFeatureId, is(ApplicationFeatureId.newMember("com.mycompany.Bar","foo")));
        }
    }

    public static class GetParentIds extends ApplicationFeatureIdTest {

        @Test
        public void whenPackageWithNoParent() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com");

            // when
            final List<ApplicationFeatureId> parentIds = applicationFeatureId.getParentIds();

            // then
            assertThat(parentIds, emptyCollectionOf(ApplicationFeatureId.class));
        }

        @Test
        public void whenPackageWithHasParent() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany");

            // when
            final List<ApplicationFeatureId> parentIds = applicationFeatureId.getParentIds();

            // then
            assertThat(parentIds, contains(ApplicationFeatureId.newPackage("com")));
        }

        @Test
        public void whenPackageWithHasParents() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany.bish.bosh");

            // when
            final List<ApplicationFeatureId> parentIds = applicationFeatureId.getParentIds();

            // then
            assertThat(parentIds, contains(
                    ApplicationFeatureId.newPackage("com.mycompany.bish"),
                    ApplicationFeatureId.newPackage("com.mycompany"),
                    ApplicationFeatureId.newPackage("com")
                    ));
        }

        @Test
        public void whenClassWithParents() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");

            // when
            final List<ApplicationFeatureId> parentIds = applicationFeatureId.getParentIds();

            // then
            assertThat(parentIds, contains(
                    ApplicationFeatureId.newPackage("com.mycompany"),
                    ApplicationFeatureId.newPackage("com")
                    ));
        }

        @Test
        public void whenMember() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");

            // when
            final List<ApplicationFeatureId> parentIds = applicationFeatureId.getParentIds();

            // then
            assertThat(parentIds, contains(
                    ApplicationFeatureId.newClass("com.mycompany.Bar"),
                    ApplicationFeatureId.newPackage("com.mycompany"),
                    ApplicationFeatureId.newPackage("com")
                    ));
        }

    }

    public static class GetParentPackageId extends ApplicationFeatureIdTest {

        @Test
        public void givenPackageWhenParentIsNotRoot() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com.mycompany");
            // when
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();
            // then
            assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(parentPackageId.getPackageName(), is("com"));
            assertThat(parentPackageId.getClassName(), is(nullValue()));
            assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenPackageWhenParentIsRoot() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com");
            // when
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();
            // then
            assertThat(parentPackageId, is(nullValue()));
        }

        @Test
        public void givenRootPackage() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("");
            // when
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();
            // then
            assertThat(parentPackageId, is(nullValue()));
        }

        @Test
        public void givenClass() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");

            // when
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            // then
            assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(parentPackageId.getPackageName(), is("com.mycompany"));
            assertThat(parentPackageId.getClassName(), is(nullValue()));
            assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenClassInRootPackage() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("Bar");

            // when
            final ApplicationFeatureId parentPackageId = applicationFeatureId.getParentPackageId();

            // then
            assertThat(parentPackageId.getType(), is(ApplicationFeatureType.PACKAGE));
            assertThat(parentPackageId.getPackageName(), is(""));
            assertThat(parentPackageId.getClassName(), is(nullValue()));
            assertThat(parentPackageId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenMember() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");

            // then
            expectedException.expect(IllegalStateException.class);

            // when
            applicationFeatureId.getParentPackageId();
        }

    }

    public static class GetParentClass extends ApplicationFeatureIdTest {

        @Test
        public void givenMember() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newMember("com.mycompany.Bar", "foo");

            // when
            final ApplicationFeatureId parentClassId = applicationFeatureId.getParentClassId();

            // then
            assertThat(parentClassId.getType(), is(ApplicationFeatureType.CLASS));
            assertThat(parentClassId.getPackageName(), is("com.mycompany"));
            assertThat(parentClassId.getClassName(), is("Bar"));
            assertThat(parentClassId.getMemberName(), is(nullValue()));
        }

        @Test
        public void givenPackage() throws Exception {
            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newPackage("com");

            // then
            expectedException.expect(IllegalStateException.class);

            // when
            applicationFeatureId.getParentClassId();
        }

        @Test
        public void givenClass() throws Exception {

            // given
            final ApplicationFeatureId applicationFeatureId = ApplicationFeatureId.newClass("com.mycompany.Bar");

            // then
            expectedException.expect(IllegalStateException.class);

            // when
            applicationFeatureId.getParentClassId();
        }
    }

    public static abstract class ValueTypeContractTest extends ValueTypeContractTestAbstract<ApplicationFeatureId> {

        public static class PackageFeatures extends ValueTypeContractTest {

            @Override
            protected List<ApplicationFeatureId> getObjectsWithSameValue() {
                return Arrays.asList(
                        ApplicationFeatureId.newPackage("com.mycompany"),
                        ApplicationFeatureId.newPackage("com.mycompany"));
            }

            @Override
            protected List<ApplicationFeatureId> getObjectsWithDifferentValue() {
                return Arrays.asList(
                        ApplicationFeatureId.newPackage("com.mycompany2"),
                        ApplicationFeatureId.newClass("com.mycompany.Foo"),
                        ApplicationFeatureId.newMember("com.mycompany.Foo#bar"));
            }
        }

        public static class ClassFeatures extends ValueTypeContractTest {

            @Override
            protected List<ApplicationFeatureId> getObjectsWithSameValue() {
                return Arrays.asList(
                        ApplicationFeatureId.newClass("com.mycompany.Foo"),
                        ApplicationFeatureId.newClass("com.mycompany.Foo"));
            }

            @Override
            protected List<ApplicationFeatureId> getObjectsWithDifferentValue() {
                return Arrays.asList(
                        ApplicationFeatureId.newPackage("com.mycompany"),
                        ApplicationFeatureId.newClass("com.mycompany.Foo2"),
                        ApplicationFeatureId.newMember("com.mycompany.Foo#bar"));
            }
        }

        public static class MemberFeatures extends ValueTypeContractTest {

            @Override
            protected List<ApplicationFeatureId> getObjectsWithSameValue() {
                return Arrays.asList(
                        ApplicationFeatureId.newMember("com.mycompany.Foo#bar"),
                        ApplicationFeatureId.newMember("com.mycompany.Foo#bar"));
            }

            @Override
            protected List<ApplicationFeatureId> getObjectsWithDifferentValue() {
                return Arrays.asList(
                        ApplicationFeatureId.newPackage("com.mycompany"),
                        ApplicationFeatureId.newClass("com.mycompany.Foo"),
                        ApplicationFeatureId.newMember("com.mycompany.Foo#bar2"));
            }
        }

    }

    public static class PrivateConstructors {

        @Test
        public void forFunctions() throws Exception {
            new PrivateConstructorTester(ApplicationFeatureId.Functions.class).exercise();
        }
        @Test
        public void forPredicates() throws Exception {
            new PrivateConstructorTester(ApplicationFeatureId.Predicates.class).exercise();
        }
        @Test
        public void forComparators() throws Exception {
            new PrivateConstructorTester(ApplicationFeatureId.Comparators.class).exercise();
        }
    }

    public static class FunctionsTest extends ApplicationFeatureIdTest {

        public static class GET_CLASS_NAME extends FunctionsTest {

            private Function<ApplicationFeatureId, String> func = ApplicationFeatureId.Functions.GET_CLASS_NAME;

            @Test
            public void whenNull() throws Exception {
                expectedException.expect(NullPointerException.class);
                func.apply(null);
            }

            @Test
            public void whenPackage() throws Exception {
                assertThat(func.apply(ApplicationFeatureId.newPackage("com.mycompany")), is(nullValue()));
            }

            @Test
            public void whenClass() throws Exception {
                assertThat(func.apply(ApplicationFeatureId.newClass("com.mycompany.Bar")), is("Bar"));
            }

            @Test
            public void whenMember() throws Exception {
                assertThat(func.apply(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")), is("Bar"));
            }

        }

        public static class GET_MEMBER_NAME extends FunctionsTest {

            private Function<ApplicationFeatureId, String> func = ApplicationFeatureId.Functions.GET_MEMBER_NAME;

            @Test
            public void whenNull() throws Exception {
                expectedException.expect(NullPointerException.class);
                func.apply(null);
            }

            @Test
            public void whenPackage() throws Exception {
                assertThat(func.apply(ApplicationFeatureId.newPackage("com.mycompany")), is(nullValue()));
            }

            @Test
            public void whenClass() throws Exception {
                assertThat(func.apply(ApplicationFeatureId.newClass("com.mycompany.Bar")), is(nullValue()));
            }

            @Test
            public void whenMember() throws Exception {
                assertThat(func.apply(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")), is("foo"));
            }

        }

    }

    public static class PredicatesTest extends ApplicationFeatureIdTest {

        public static class IsClassContaining extends PredicatesTest {

            private ApplicationMemberType memberType;

            @Mock
            private ApplicationFeatureRepository mockApplicationFeatureRepository;
            @Mock
            private ApplicationFeature mockApplicationFeature;

            @Test
            public void whenNull() throws Exception {
                expectedException.expect(NullPointerException.class);

                ApplicationFeatureId.Predicates.
                        isClassContaining(ApplicationMemberType.ACTION, mockApplicationFeatureRepository).
                        apply(null);
            }

            @Test
            public void whenNotClass() throws Exception {
                assertThat(
                        ApplicationFeatureId.Predicates.
                                isClassContaining(ApplicationMemberType.ACTION, mockApplicationFeatureRepository).
                                apply(ApplicationFeatureId.newPackage("com.mycompany")),
                        is(false));
                assertThat(
                        ApplicationFeatureId.Predicates.
                                isClassContaining(ApplicationMemberType.ACTION, mockApplicationFeatureRepository).
                                apply(ApplicationFeatureId.newMember("com.mycompany.Bar#foo")),
                        is(false));
            }

            @Test
            public void whenClassButFeatureNotFound() throws Exception {
                final ApplicationFeatureId classFeature = ApplicationFeatureId.newClass("com.mycompany.Bar");
                context.checking(new Expectations() {{
                    allowing(mockApplicationFeatureRepository).findFeature(classFeature);
                    will(returnValue(null));
                }});

                assertThat(
                        ApplicationFeatureId.Predicates.
                                isClassContaining(ApplicationMemberType.ACTION, mockApplicationFeatureRepository).
                                apply(classFeature),
                        is(false));
            }
            @Test
            public void whenClassAndFeatureNotFoundButHasNoMembersOfType() throws Exception {
                final ApplicationFeatureId classFeature = ApplicationFeatureId.newClass("com.mycompany.Bar");
                context.checking(new Expectations() {{
                    oneOf(mockApplicationFeatureRepository).findFeature(classFeature);
                    will(returnValue(mockApplicationFeature));

                    allowing(mockApplicationFeature).membersOf(ApplicationMemberType.ACTION);
                    will(returnValue(new TreeSet<>()));
                }});

                assertThat(
                        ApplicationFeatureId.Predicates.
                                isClassContaining(ApplicationMemberType.ACTION, mockApplicationFeatureRepository).
                                apply(classFeature),
                        is(false));
            }
            @Test
            public void whenClassAndFeatureNotFoundAndHasMembersOfType() throws Exception {
                final ApplicationFeatureId classFeature = ApplicationFeatureId.newClass("com.mycompany.Bar");
                context.checking(new Expectations() {{
                    oneOf(mockApplicationFeatureRepository).findFeature(classFeature);
                    will(returnValue(mockApplicationFeature));

                    allowing(mockApplicationFeature).membersOf(ApplicationMemberType.ACTION);
                    will(returnValue(new TreeSet<ApplicationFeatureId>() {{
                        add(ApplicationFeatureId.newMember("com.mycompany.Bar#foo"));
                    }}));
                }});

                assertThat(
                        ApplicationFeatureId.Predicates.
                                isClassContaining(ApplicationMemberType.ACTION, mockApplicationFeatureRepository).
                                apply(classFeature),
                        is(true));
            }
        }
    }

    public static class Serialization extends SerializationContractTest {

        @Test
        public void roundtrip() throws Exception {

            final ApplicationFeatureId original = ApplicationFeatureId.newClass("com.foo.Bar");

            final ApplicationFeatureId roundtripped = roundtripSerialization(original);

            assertThat(roundtripped.getFullyQualifiedName(), is("com.foo.Bar"));
        }

    }

}