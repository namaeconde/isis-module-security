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
package org.isisaddons.module.security.app.user;

import java.nio.charset.Charset;
import java.util.Iterator;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.io.BaseEncoding;

import org.apache.isis.applib.DomainObjectContainer;
import org.apache.isis.applib.ViewModel;
import org.apache.isis.applib.annotation.BookmarkPolicy;
import org.apache.isis.applib.annotation.Editing;
import org.apache.isis.applib.annotation.MemberGroupLayout;
import org.apache.isis.applib.annotation.MemberOrder;
import org.apache.isis.applib.annotation.Programmatic;
import org.apache.isis.applib.annotation.Property;
import org.apache.isis.applib.annotation.PropertyLayout;
import org.apache.isis.applib.annotation.ViewModelLayout;
import org.apache.isis.applib.annotation.Where;
import org.apache.isis.applib.util.ObjectContracts;

import org.isisaddons.module.security.SecurityModule;
import org.isisaddons.module.security.app.feature.ApplicationFeatureViewModel;
import org.isisaddons.module.security.dom.feature.ApplicationFeature;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureId;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureRepository;
import org.isisaddons.module.security.dom.feature.ApplicationFeatureType;
import org.isisaddons.module.security.dom.permission.ApplicationPermission;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionMode;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRepository;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionRule;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionValue;
import org.isisaddons.module.security.dom.permission.ApplicationPermissionValueSet;
import org.isisaddons.module.security.dom.user.ApplicationUser;
import org.isisaddons.module.security.dom.user.ApplicationUserRepository;

/**
 * View model identified by {@link org.isisaddons.module.security.dom.feature.ApplicationFeatureId} and backed by an
 * {@link org.isisaddons.module.security.dom.feature.ApplicationFeature}.
 */
@SuppressWarnings("UnusedDeclaration")
@ViewModelLayout(
    bookmarking = BookmarkPolicy.AS_ROOT
)
@MemberGroupLayout(
        columnSpans = {6,0,6,0},
        left = {"Permission"},
        right= {"Cause"}
)
public class UserPermissionViewModel implements ViewModel {

    public static abstract class PropertyDomainEvent<T> extends SecurityModule.PropertyDomainEvent<UserPermissionViewModel, T> {}

    public static abstract class CollectionDomainEvent<T> extends SecurityModule.CollectionDomainEvent<UserPermissionViewModel, T> {}

    public static abstract class ActionDomainEvent extends SecurityModule.ActionDomainEvent<UserPermissionViewModel> {}

    // //////////////////////////////////////

    private static final int TYPICAL_LENGTH_VERB = 12;

    //region > constructors, factory methods
    public static UserPermissionViewModel newViewModel(
            final ApplicationFeatureId featureId, final ApplicationUser user, final ApplicationPermissionValueSet.Evaluation viewingEvaluation, final ApplicationPermissionValueSet.Evaluation changingEvaluation, final DomainObjectContainer container) {
        return container.newViewModelInstance(UserPermissionViewModel.class, asEncodedString(featureId, user.getUsername(), viewingEvaluation, changingEvaluation));
    }

    public UserPermissionViewModel() {
    }
    //endregion

    // //////////////////////////////////////

    //region > identification
    public String title() {
        return getVerb() + " " + getFeatureId().getFullyQualifiedName();
    }

    public String iconName() {
        return "userPermission";
    }
    //endregion

    // //////////////////////////////////////

    //region > ViewModel impl
    @Override
    public String viewModelMemento() {
        return asEncodedString();
    }

    @Override
    public void viewModelInit(final String encodedMemento) {
        parseEncoded(encodedMemento);
    }

    private static String asEncodedString(
            final ApplicationFeatureId featureId,
            final String username,
            final ApplicationPermissionValueSet.Evaluation viewingEvaluation,
            final ApplicationPermissionValueSet.Evaluation changingEvaluation) {
        return base64UrlEncode(asString(featureId, username, viewingEvaluation, changingEvaluation));
    }

    private static String asString(
            final ApplicationFeatureId featureId,
            final String username,
            final ApplicationPermissionValueSet.Evaluation viewingEvaluation,
            final ApplicationPermissionValueSet.Evaluation changingEvaluation) {

        final boolean viewingEvaluationGranted = viewingEvaluation.isGranted();
        final ApplicationPermissionValue viewingEvaluationCause = viewingEvaluation.getCause();
        final ApplicationFeatureId viewingEvaluationCauseFeatureId = viewingEvaluationCause != null? viewingEvaluationCause.getFeatureId(): null;

        final boolean changingEvaluationGranted = changingEvaluation.isGranted();
        final ApplicationPermissionValue changingEvaluationCause = changingEvaluation.getCause();
        final ApplicationFeatureId changingEvaluationCauseFeatureId = changingEvaluationCause != null? changingEvaluationCause.getFeatureId(): null;
        return Joiner.on(":").join(
                username,

                viewingEvaluationGranted,
                viewingEvaluationCauseFeatureId != null? viewingEvaluationCauseFeatureId.getType(): "",
                viewingEvaluationCauseFeatureId != null? viewingEvaluationCauseFeatureId.getFullyQualifiedName(): "",
                viewingEvaluationCause != null? viewingEvaluationCause.getRule(): "",
                viewingEvaluationCause != null? viewingEvaluationCause.getMode(): "",

                changingEvaluationGranted,
                changingEvaluationCauseFeatureId != null? changingEvaluationCauseFeatureId.getType(): "",
                changingEvaluationCauseFeatureId != null? changingEvaluationCauseFeatureId.getFullyQualifiedName(): "",
                changingEvaluationCause != null? changingEvaluationCause.getRule(): "",
                changingEvaluationCause != null? changingEvaluationCause.getMode(): "",

                featureId.getType(), featureId.getFullyQualifiedName());
    }

    private void parseEncoded(final String encodedString) {
        parse(base64UrlDecode(encodedString));
    }

    private void parse(final String asString) {
        final Iterator<String> iterator = Splitter.on(":").split(asString).iterator();

        this.username = iterator.next();

        this.viewingGranted = Boolean.valueOf(iterator.next());
        final String viewingEvaluationCauseFeatureIdType = iterator.next();
        final ApplicationFeatureType viewingEvaluationFeatureIdType =  !viewingEvaluationCauseFeatureIdType.isEmpty() ? ApplicationFeatureType.valueOf(viewingEvaluationCauseFeatureIdType) : null;
        final String viewingEvaluationFeatureFqn = iterator.next();
        this.viewingFeatureId = viewingEvaluationFeatureIdType != null? new ApplicationFeatureId(viewingEvaluationFeatureIdType,viewingEvaluationFeatureFqn) : null;

        final String viewingEvaluationCauseRule = iterator.next();
        this.viewingRule = !viewingEvaluationCauseRule.isEmpty()? ApplicationPermissionRule.valueOf(viewingEvaluationCauseRule): null;
        final String viewingEvaluationCauseMode = iterator.next();
        this.viewingMode = !viewingEvaluationCauseMode.isEmpty()? ApplicationPermissionMode.valueOf(viewingEvaluationCauseMode): null;


        this.changingGranted = Boolean.valueOf(iterator.next());
        final String changingEvaluationCauseFeatureIdType = iterator.next();
        final ApplicationFeatureType changingEvaluationFeatureIdType =  !changingEvaluationCauseFeatureIdType.isEmpty() ? ApplicationFeatureType.valueOf(changingEvaluationCauseFeatureIdType) : null;
        final String changingEvaluationFeatureFqn = iterator.next();
        this.changingFeatureId = changingEvaluationFeatureIdType != null? new ApplicationFeatureId(changingEvaluationFeatureIdType,changingEvaluationFeatureFqn) : null;

        final String changingEvaluationCauseRule = iterator.next();
        this.changingRule = !changingEvaluationCauseRule.isEmpty()? ApplicationPermissionRule.valueOf(changingEvaluationCauseRule): null;
        final String changingEvaluationCauseMode = iterator.next();
        this.changingMode = !changingEvaluationCauseMode.isEmpty()? ApplicationPermissionMode.valueOf(changingEvaluationCauseMode): null;

        final ApplicationFeatureType type = ApplicationFeatureType.valueOf(iterator.next());
        this.featureId = new ApplicationFeatureId(type, iterator.next());
    }

    // //////////////////////////////////////

    @Programmatic
    public String asEncodedString() {
        return asEncodedString(getFeatureId(), getUsername(), newEvaluation(viewingGranted, viewingFeatureId, viewingRule, viewingMode), newEvaluation(changingGranted, changingFeatureId, changingRule, changingMode));
    }

    private static ApplicationPermissionValueSet.Evaluation newEvaluation(final boolean granted, final ApplicationFeatureId featureId, final ApplicationPermissionRule rule, final ApplicationPermissionMode mode) {
        return new ApplicationPermissionValueSet.Evaluation(newPermissionValue(featureId, rule, mode), granted);
    }

    private static ApplicationPermissionValue newPermissionValue(final ApplicationFeatureId featureId, final ApplicationPermissionRule rule, final ApplicationPermissionMode mode) {
        if(featureId == null || mode == null || rule == null) {
            return null;
        } else {
            return new ApplicationPermissionValue(featureId, rule, mode);
        }
    }

    private static String base64UrlDecode(final String str) {
        final byte[] bytes = BaseEncoding.base64Url().decode(str);
        return new String(bytes, Charset.forName("UTF-8"));
    }

    private static String base64UrlEncode(final String str) {
        final byte[] bytes = str.getBytes(Charset.forName("UTF-8"));
        return BaseEncoding.base64Url().encode(bytes);
    }

    //endregion

    // //////////////////////////////////////

    //region > user (derived property, hidden in parented tables)
    public static class UserDomainEvent extends PropertyDomainEvent<ApplicationUser> {}

    @Property(
            domainEvent = UserDomainEvent.class
    )
    @PropertyLayout(hidden=Where.PARENTED_TABLES)
    @MemberOrder(name = "Permission", sequence = "1")
    public ApplicationUser getUser() {
        return applicationUserRepository.findOrCreateUserByUsername(getUsername());
    }

    private String username;
    @Programmatic
    public String getUsername() {
        return username;
    }
    //endregion

    // //////////////////////////////////////

    //region > verb (derived property)

    public static class VerbDomainEvent extends PropertyDomainEvent<String> {}

    private boolean viewingGranted;
    private boolean changingGranted;

    @Property(
            domainEvent = VerbDomainEvent.class
    )
    @PropertyLayout(typicalLength=UserPermissionViewModel.TYPICAL_LENGTH_VERB)
    @MemberOrder(name="Permission", sequence = "2")
    public String getVerb() {
        return changingGranted
                ? "Can change"
                : viewingGranted
                ? "Can view"
                : "No access to";
    }
    //endregion

    // //////////////////////////////////////

    //region > feature (derived property)

    public static class FeatureDomainEvent extends PropertyDomainEvent<ApplicationFeatureViewModel> {}

    @javax.jdo.annotations.NotPersistent
    @Property(
            domainEvent = FeatureDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(hidden=Where.REFERENCES_PARENT)
    @MemberOrder(name = "Permission",sequence = "4")
    public ApplicationFeatureViewModel getFeature() {
        if(getFeatureId() == null) {
            return null;
        }
        return ApplicationFeatureViewModel.newViewModel(getFeatureId(), applicationFeatureRepository, container);
    }

    // //////////////////////////////////////

    private ApplicationFeatureId featureId;

    @Programmatic
    public ApplicationFeatureId getFeatureId() {
        return featureId;
    }

    public void setFeatureId(final ApplicationFeatureId applicationFeatureId) {
        this.featureId = applicationFeatureId;
    }

    //endregion

    // //////////////////////////////////////

    //region > viewingPermission (derived property)

    public static class ViewingPermissionDomainEvent extends PropertyDomainEvent<ApplicationPermission> {}

    private ApplicationFeatureId viewingFeatureId;
    private ApplicationPermissionMode viewingMode;
    private ApplicationPermissionRule viewingRule;

    @javax.jdo.annotations.NotPersistent
    @Property(
            domainEvent = ViewingPermissionDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(hidden=Where.REFERENCES_PARENT)
    @MemberOrder(name="Cause", sequence = "2.1")
    public ApplicationPermission getViewingPermission() {
        if(getViewingPermissionValue() == null) {
            return null;
        }
        return applicationPermissionRepository.findByUserAndPermissionValue(username, getViewingPermissionValue());
    }

    private ApplicationPermissionValue getViewingPermissionValue() {
        if(viewingFeatureId == null) {
            return null;
        }
        return new ApplicationPermissionValue(viewingFeatureId, viewingRule, viewingMode);
    }

    //endregion

    // //////////////////////////////////////

    //region > changingPermission (derived property)

    public static class ChangingPermissionDomainEvent extends PropertyDomainEvent<ApplicationPermission> {}

    private ApplicationFeatureId changingFeatureId;
    private ApplicationPermissionMode changingMode;
    private ApplicationPermissionRule changingRule;


    @javax.jdo.annotations.NotPersistent
    @Property(
            domainEvent = ChangingPermissionDomainEvent.class,
            editing = Editing.DISABLED
    )
    @PropertyLayout(hidden=Where.REFERENCES_PARENT)
    @MemberOrder(name="Cause", sequence = "2.2")
    public ApplicationPermission getChangingPermission() {
        if(getChangingPermissionValue() == null) {
            return null;
        }
        return applicationPermissionRepository.findByUserAndPermissionValue(username, getChangingPermissionValue());
    }

    private ApplicationPermissionValue getChangingPermissionValue() {
        if(changingFeatureId == null) {
            return null;
        }
        return new ApplicationPermissionValue(changingFeatureId, changingRule, changingMode);
    }

    //endregion

    // //////////////////////////////////////

    //region > toString

    private final static String propertyNames = "user, featureId";

    @Override
    public String toString() {
        return ObjectContracts.toString(this, propertyNames);
    }
    //endregion

    // //////////////////////////////////////

    //region > Functions

    public static class Functions {
        private Functions(){}
        public static Function<ApplicationFeature, UserPermissionViewModel> asViewModel(final ApplicationUser user, final DomainObjectContainer container) {
            return new Function<ApplicationFeature, UserPermissionViewModel>(){
                @Override
                public UserPermissionViewModel apply(final ApplicationFeature input) {
                    final ApplicationPermissionValueSet permissionSet = user.getPermissionSet();
                    final ApplicationPermissionValueSet.Evaluation changingEvaluation = permissionSet.evaluate(input.getFeatureId(), ApplicationPermissionMode.CHANGING);
                    final ApplicationPermissionValueSet.Evaluation viewingEvaluation = permissionSet.evaluate(input.getFeatureId(), ApplicationPermissionMode.VIEWING);
                    return UserPermissionViewModel.newViewModel(input.getFeatureId(), user, viewingEvaluation, changingEvaluation, container);
                }
            };
        }
    }
    //endregion

    // //////////////////////////////////////

    //region > injected services
    @javax.inject.Inject
    DomainObjectContainer container;

    @javax.inject.Inject
    ApplicationFeatureRepository applicationFeatureRepository;

    @javax.inject.Inject
    ApplicationPermissionRepository applicationPermissionRepository;

    @javax.inject.Inject
    ApplicationUserRepository applicationUserRepository;
    //endregion

}
