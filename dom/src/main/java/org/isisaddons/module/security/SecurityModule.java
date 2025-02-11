/*
 *  Copyright 2013~2015 Dan Haywood
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
package org.isisaddons.module.security;

public final class SecurityModule {

    private SecurityModule(){}

    public abstract static class ActionDomainEvent<S> extends org.apache.isis.applib.services.eventbus.ActionDomainEvent<S> {
    }

    public abstract static class CollectionDomainEvent<S,T> extends org.apache.isis.applib.services.eventbus.CollectionDomainEvent<S,T> {
    }

    public abstract static class PropertyDomainEvent<S,T> extends org.apache.isis.applib.services.eventbus.PropertyDomainEvent<S,T> {
    }
}
