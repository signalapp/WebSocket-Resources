/**
 * Copyright (C) 2014 Open WhisperSystems
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.whispersystems.websocket.session;

import com.sun.jersey.api.core.HttpContext;
import com.sun.jersey.api.model.Parameter;
import com.sun.jersey.core.spi.component.ComponentContext;
import com.sun.jersey.core.spi.component.ComponentScope;
import com.sun.jersey.server.impl.inject.AbstractHttpContextInjectable;
import com.sun.jersey.spi.inject.Injectable;
import com.sun.jersey.spi.inject.InjectableProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.whispersystems.websocket.servlet.WebSocketServletRequest;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Response;
import java.security.Principal;

public class WebSocketSessionContextProvider implements InjectableProvider<WebSocketSession, Parameter> {

  private static final Logger logger = LoggerFactory.getLogger(WebSocketSessionContextProvider.class);

  @Override
  public ComponentScope getScope() {
    return ComponentScope.PerRequest;
  }

  @Override
  public Injectable<?> getInjectable(ComponentContext ic, WebSocketSession session, Parameter parameter) {
    return new WebSocketClientInjectable();
  }

  private static class WebSocketClientInjectable extends AbstractHttpContextInjectable<WebSocketSessionContext>
  {

    @Override
    public WebSocketSessionContext getValue(HttpContext c) {
      Principal principal = c.getRequest().getUserPrincipal();

      if (principal instanceof WebSocketServletRequest.ContextPrincipal) {
        return ((WebSocketServletRequest.ContextPrincipal)principal).getContext();
      }

      throw new WebApplicationException(Response.status(Response.Status.NOT_ACCEPTABLE)
                                                .entity("WebSockets Only")
                                                .build());
    }
  }

}
