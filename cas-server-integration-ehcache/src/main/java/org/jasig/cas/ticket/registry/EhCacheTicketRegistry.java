/*
 * Copyright 2009-2011 The JA-SIG Collaborative. All rights reserved. See license
 * distributed with this file and available online at
 * http://www.uportal.org/license.html
 */
package org.jasig.cas.ticket.registry;

import java.util.Collection;

import net.sf.ehcache.Cache;
import net.sf.ehcache.Element;
import net.sf.ehcache.config.CacheConfiguration;

import org.jasig.cas.ticket.ServiceTicket;
import org.jasig.cas.ticket.Ticket;
import org.jasig.cas.ticket.TicketGrantingTicket;
import org.springframework.beans.BeanInstantiationException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.style.ToStringCreator;

/**
 * <p>
 * <a href="http://ehcache.org/">Ehcache</a> based distributed ticket registry.
 * </p>
 * <p>
 * Use distinct caches for ticket granting tickets (TGT) and service tickets (ST) for:
 * <ul>
 * <li>Tuning : use cache level time to live with different values for TGT an ST.</li>
 * <li>Monitoring : follow separately the number of TGT and ST.</li>
 * <ul>
 * </p>
 * 
 * @author <a href="mailto:cleclerc@xebia.fr">Cyrille Le Clerc</a>
 * @author Adam Rybicki
 * @author Andrew Tillinghast
 */
public final class EhCacheTicketRegistry extends AbstractDistributedTicketRegistry implements InitializingBean {
    
    private Cache serviceTicketsCache;
    
    private Cache ticketGrantingTicketsCache;
    
    public void addTicket(final Ticket ticket) {
        Element element = new Element(ticket.getId(), ticket);
        if (ticket instanceof ServiceTicket) {
            this.serviceTicketsCache.put(element);
        } else if (ticket instanceof TicketGrantingTicket) {
            this.ticketGrantingTicketsCache.put(element);
        } else {
            throw new IllegalArgumentException("Invalid ticket type " + ticket);
        }
    }
    
    public boolean deleteTicket(final String ticketId) {
        if (ticketId == null) {
            return false;
        }
        return this.serviceTicketsCache.remove(ticketId) || this.ticketGrantingTicketsCache.remove(ticketId);
    }
    
    public Ticket getTicket(final String ticketId) {
        if (ticketId == null) {
            return null;
        }

        Element element = this.serviceTicketsCache.get(ticketId);
        if (element == null) {
            element = this.ticketGrantingTicketsCache.get(ticketId);
        }
        return element == null ? null : getProxiedTicketInstance((Ticket)element.getValue());
    }
    
    public Collection<Ticket> getTickets() {
        throw new UnsupportedOperationException("GetTickets not supported.");
    }
    
    public void setServiceTicketsCache(Cache serviceTicketsCache) {
        this.serviceTicketsCache = serviceTicketsCache;
    }
    
    public void setTicketGrantingTicketsCache(Cache ticketGrantingTicketsCache) {
        this.ticketGrantingTicketsCache = ticketGrantingTicketsCache;
    }
    
    @Override
    public String toString() {
        return new ToStringCreator(this).append("ticketGrantingTicketsCache", this.ticketGrantingTicketsCache)
            .append("serviceTicketsCache", this.serviceTicketsCache).toString();
    }
    
    @Override
    protected void updateTicket(Ticket ticket) {
        addTicket(ticket);
    }
    
    @Override
    protected boolean needsCallback(){
    	return false;
    }

    public void afterPropertiesSet() throws Exception {
      if (this.serviceTicketsCache == null || this.ticketGrantingTicketsCache == null) {
        String message = "Both serviceTicketsCache and ticketGrantingTicketsCache are required properties. serviceTicketsCache=" + this.serviceTicketsCache + ", ticketGrantingTicketsCache=" + this.ticketGrantingTicketsCache;
        log.error(message);
        throw new BeanInstantiationException(this.getClass(), message);
      }
      if(log.isDebugEnabled()) {
        CacheConfiguration config = this.serviceTicketsCache.getCacheConfiguration();
        log.debug("serviceTicketsCache.maxElementsInMemory=" + config.getMaxElementsInMemory());
        log.debug("serviceTicketsCache.maxElementsOnDisk=" + config.getMaxElementsOnDisk());
        log.debug("serviceTicketsCache.overflowToDisk=" + config.isOverflowToDisk());
        log.debug("serviceTicketsCache.timeToLive=" + config.getTimeToLiveSeconds());
        log.debug("serviceTicketsCache.timeToIdle=" + config.getTimeToIdleSeconds());
        config = this.ticketGrantingTicketsCache.getCacheConfiguration();
        log.debug("ticketGrantingTicketsCache.maxElementsInMemory=" + config.getMaxElementsInMemory());
        log.debug("ticketGrantingTicketsCache.maxElementsOnDisk=" + config.getMaxElementsOnDisk());
        log.debug("ticketGrantingTicketsCache.overflowToDisk=" + config.isOverflowToDisk());
        log.debug("ticketGrantingTicketsCache.timeToLive=" + config.getTimeToLiveSeconds());
        log.debug("ticketGrantingTicketsCache.timeToIdle=" + config.getTimeToIdleSeconds());
      }
    }
}