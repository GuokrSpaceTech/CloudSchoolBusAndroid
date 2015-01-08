package com.cytx.domain;

/**
 * 获取问题历史信息：其中的problem
 * @author xilehang
 *
 */
public class QuestionHistoryProblemDomain {

	private long id;
	private String status;
	private float price;
	private boolean to_doc;
	private String title;
	private String ask;
	private String clinic_no;
	private String clinic_name;
	private boolean is_viewed;
	private boolean need_assess;
	private long created_time_ms;
	private String created_time;
	private int star;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public float getPrice() {
		return price;
	}

	public void setPrice(float price) {
		this.price = price;
	}

	public boolean isTo_doc() {
		return to_doc;
	}

	public void setTo_doc(boolean to_doc) {
		this.to_doc = to_doc;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAsk() {
		return ask;
	}

	public void setAsk(String ask) {
		this.ask = ask;
	}

	public String getClinic_no() {
		return clinic_no;
	}

	public void setClinic_no(String clinic_no) {
		this.clinic_no = clinic_no;
	}

	public String getClinic_name() {
		return clinic_name;
	}

	public void setClinic_name(String clinic_name) {
		this.clinic_name = clinic_name;
	}

	public boolean isIs_viewed() {
		return is_viewed;
	}

	public void setIs_viewed(boolean is_viewed) {
		this.is_viewed = is_viewed;
	}

	public boolean isNeed_assess() {
		return need_assess;
	}

	public void setNeed_assess(boolean need_assess) {
		this.need_assess = need_assess;
	}

	public long getCreated_time_ms() {
		return created_time_ms;
	}

	public void setCreated_time_ms(long created_time_ms) {
		this.created_time_ms = created_time_ms;
	}

	public String getCreated_time() {
		return created_time;
	}

	public void setCreated_time(String created_time) {
		this.created_time = created_time;
	}

	public int getStar() {
		return star;
	}

	public void setStar(int star) {
		this.star = star;
	}

}
