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

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.annotation.Action;
import org.apache.isis.applib.annotation.ActionLayout;
import org.apache.isis.applib.annotation.Contributed;
import org.apache.isis.applib.annotation.DomainService;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.NatureOfService;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.SemanticsOf;
import org.apache.isis.applib.annotation.Where;

import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureRepository;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;

@DomainService(
        nature = NatureOfService.VIEW_CONTRIBUTIONS_ONLY
)
public class ApplicationFeatureViewModelContributions {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<ApplicationFeatureViewModelContributions, T> {}

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<ApplicationFeatureViewModelContributions, T> {}

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<ApplicationFeatureViewModelContributions> {}

    // //////////////////////////////////////

    //region > feature
    public static class FeatureDomainEvent extends PropertyDomainEvent<ApplicationFeatureViewModel> {}

    @Action(
            semantics = SemanticsOf.SAFE
    )
    @ActionLayout(
            contributed = Contributed.AS_ASSOCIATION
    )
    @Property(
            domainEvent = FeatureDomainEvent.class
    )
    @PropertyLayout(
            hidden=Where.REFERENCES_PARENT
    )
    @MemberOrder(name="Feature", sequence = "4")
    public ApplicationFeatureViewModel feature(final ApplicationPermission permission) {
        if(permission.getFeatureType() == null) {
            return null;
        }
        final ApplicationFeatureId featureId = getFeatureId(permission);
        return ApplicationFeatureViewModel.newViewModel(featureId, applicationFeatureRepository, container);
    }

    private static ApplicationFeatureId getFeatureId(final ApplicationPermission permission) {
        return ApplicationFeatureId.newFeature(permission.getFeatureType(), permission.getFeatureFqn());
    }

    //endregion

    //region  > services (injected)
    @javax.inject.Inject
    DomainObjectContainer container;

    @javax.inject.Inject
    ApplicationFeatureRepository applicationFeatureRepository;

    //endregion

}
