# SpringAngular
'
package com.accedian.cvets.web.rest;

import com.accedian.cvets.domain.CVEInfo;
import com.accedian.cvets.repository.CVEInfoRepository;
import com.accedian.cvets.repository.search.CVEInfoSearchRepository;
import com.accedian.cvets.web.rest.util.PaginationUtil;
import com.codahale.metrics.annotation.Timed;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import javax.validation.Valid;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

import static org.elasticsearch.index.query.QueryBuilders.queryString;

/**
 * REST controller for managing CVEInfo.
 */
@RestController
@RequestMapping("/api")
public class CVEInfoResource {

	private final Logger log = LoggerFactory.getLogger(CVEInfoResource.class);

	@Inject
	private CVEInfoRepository cveinfoRepository;

	@Inject
	private CVEInfoSearchRepository cveinfoSearchRepository;

	/**
	 * POST  /cveinfos -> Create a new cveinfo.
	 */
	@RequestMapping(value = "/cveinfos",
					method = RequestMethod.POST,
					produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> create(@Valid @RequestBody CVEInfo cveinfo)
					throws URISyntaxException {
		log.debug("REST request to save CVEInfo : {}", cveinfo);
		if (cveinfo.getId() != null) {
			return ResponseEntity.badRequest().header("Failure", "A new cveinfo cannot already have an ID").build();
		}
		cveinfoRepository.save(cveinfo);
		cveinfo.setProductLine(null); // can't save this with elastic
		cveinfoSearchRepository.save(cveinfo);
		return ResponseEntity.created(new URI("/api/cveinfos/" + cveinfo.getId())).build();
	}

	/**
	 * PUT  /cveinfos -> Updates an existing cveinfo.
	 */
	@RequestMapping(value = "/cveinfos",
					method = RequestMethod.PUT,
					produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<Void> update(@Valid @RequestBody CVEInfo cveinfo)
					throws URISyntaxException {
		log.debug("REST request to update CVEInfo : {}", cveinfo);
		if (cveinfo.getId() == null) {
			return create(cveinfo);
		}
		cveinfoRepository.save(cveinfo);
		cveinfo.setProductLine(null); // can't save this with elastic
		cveinfoSearchRepository.save(cveinfo);
		return ResponseEntity.ok().build();
	}

	/**
	 * GET  /cveinfos -> get all the cveinfos.
	 */
	@RequestMapping(value = "/cveinfos",
					method = RequestMethod.GET,
					produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<List<CVEInfo>> getAll(
					@RequestParam(value = "page", required = false)
					Integer offset,
					@RequestParam(value = "per_page", required = false)
					Integer limit,
					@RequestParam(value = "sort_by_risk", required = false, defaultValue = "false")
					Boolean sortByRisk,
					@RequestParam(value = "sort_by_report_date", required = false, defaultValue = "false")
					Boolean sortByReportDate) throws URISyntaxException {
		PageRequest pageRequest;

		// if sort value is true we will do DESC sort, else we do ASC
		Sort rskSort = null, rptSort=null;
		if (sortByRisk !=null && sortByRisk) {
			rskSort = new Sort(Sort.Direction.DESC, "risk");
		}
		if (sortByReportDate !=null && sortByReportDate) {
			rptSort = new Sort(Sort.Direction.DESC, "reportingDate");
		}

		if (rskSort!=null && rptSort!=null) {
			rskSort = rskSort.and(rptSort);
		} else if (rptSort!=null) {
			rskSort = rptSort;
		}

		if (rskSort!=null) {
			pageRequest = new PageRequest(offset - 1, limit, rskSort);
		} else {
			pageRequest = new PageRequest(offset - 1, limit);
		}

		Page<CVEInfo> page = cveinfoRepository.findAll(pageRequest);
		HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(page, "/api/cveinfos", offset, limit);
		return new ResponseEntity<>(page.getContent(), headers, HttpStatus.OK);
	}

	/**
	 * GET  /cveinfos/:id -> get the "id" cveinfo.
	 */
	@RequestMapping(value = "/cveinfos/{id}",
					method = RequestMethod.GET,
					produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public ResponseEntity<CVEInfo> get(@PathVariable Long id) {
		log.debug("REST request to get CVEInfo : {}", id);
		return Optional.ofNullable(cveinfoRepository.findOneWithEagerRelationships(id)).map(cveinfo -> new ResponseEntity<>(cveinfo, HttpStatus.OK)).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
	}

	/**
	 * DELETE  /cveinfos/:id -> delete the "id" cveinfo.
	 */
	@RequestMapping(value = "/cveinfos/{id}",
					method = RequestMethod.DELETE,
					produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public void delete(@PathVariable Long id) {
		log.debug("REST request to delete CVEInfo : {}", id);
		cveinfoRepository.delete(id);
		cveinfoSearchRepository.delete(id);
	}

	/**
	 * SEARCH  /_search/cveinfos/:query -> search for the cveinfo corresponding
	 * to the query.
	 */
	@RequestMapping(value = "/_search/cveinfos/{query}",
					method = RequestMethod.GET,
					produces = MediaType.APPLICATION_JSON_VALUE)
	@Timed
	public List<CVEInfo> search(@PathVariable String query) {
		return StreamSupport.stream(cveinfoSearchRepository.search(queryString(query)).spliterator(), false).collect(Collectors.toList());
	}
}


'
