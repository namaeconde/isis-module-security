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
package org.isisaddons.module.security.app.feature;

import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.ViewModelLayout;

import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;

@SuppressWarnings("UnusedDeclaration")
@ViewModelLayout(paged=100)
public class ApplicationClassCollection extends ApplicationClassMember {

    public static abstract class PropertyDomainEvent<T> extends ApplicationClassMember.PropertyDomainEvent<ApplicationClassCollection, T> {}

    public static abstract class CollectionDomainEvent<T> extends ApplicationClassMember.CollectionDomainEvent<ApplicationClassCollection, T> {}

    public static abstract class ActionDomainEvent extends ApplicationClassMember.ActionDomainEvent<ApplicationClassCollection> {}

    // //////////////////////////////////////

    //region > constructors

    public ApplicationClassCollection() {}

    public ApplicationClassCollection(final ApplicationFeatureId featureId) {
        super(featureId);
    }
    //endregion

    // //////////////////////////////////////

    //region > returnType

    public static class ElementTypeDomainEvent extends PropertyDomainEvent<String> {}

    @Property(
            domainEvent = ElementTypeDomainEvent.class
    )
    @MemberOrder(name="Data Type", sequence = "2.6")
    public String getElementType() {
        return getFeature().getReturnTypeName();
    }
    //endregion

    //region > derived

    public static class DerivedDomainEvent extends PropertyDomainEvent<Boolean> {}

    @Property(
            domainEvent = DerivedDomainEvent.class
    )
    @MemberOrder(name="Detail", sequence = "2.7")
    public boolean isDerived() {
        return getFeature().isDerived();
    }
    //endregion

}
