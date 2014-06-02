from peewee import *

import os

import MySQLdb as mysql 

''' This file contains definitions for all tables of original wikipedia and our project tables: WW2Article and SimpleGraph so far. '''

class LatinMySQLDatabase(MySQLDatabase):
    def _connect(self, database, **kwargs):
        if not mysql:
            raise ImproperlyConfigured('MySQLdb must be installed.')
        conn_kwargs = {
            'charset': 'utf8',
            'use_unicode': True,
        }
        conn_kwargs.update(kwargs)
        db = mysql.connect(db=database, **conn_kwargs)
        cursor = db.cursor()

        # hack for utf-8 characters - with set names and set characters works on my machine :P
        cursor.execute("SET NAMES latin1;")
        cursor.execute("SET CHARACTER SET latin1;")
        cursor.close()
        return db

# db conf fetched from envs (should be set by build_graph.sh)
database = LatinMySQLDatabase('wikipedia', **{'passwd': os.environ['DB_PASS'], 'user': os.environ['DB_USER'], 'host': os.environ['DB_HOST']})

class UnknownField(object):
    pass

class BaseModel(Model):
    class Meta:
        database = database

class WW2Article(BaseModel):
    id = PrimaryKeyField(db_column='id')
    title = CharField(max_length=255)
    text = TextField()

    class Meta:
        db_table = 'ww2_article'

class SimpleGraph(BaseModel):
    id = PrimaryKeyField(db_column='id')
    from_person = IntegerField()
    to_person = IntegerField()
    weight = IntegerField()
    from_name = CharField(max_length=255)
    to_name = CharField(max_length=255)

    class Meta:
        db_table = 'simple_graph'

class Archive(BaseModel):
    ar_comment = TextField()
    ar_content_format = CharField(max_length=64, null=True)
    ar_content_model = CharField(max_length=32, null=True)
    ar_deleted = IntegerField()
    ar_flags = TextField()
    ar = PrimaryKeyField(db_column='ar_id')
    ar_len = IntegerField(null=True)
    ar_minor_edit = IntegerField()
    ar_namespace = IntegerField()
    ar_page = IntegerField(db_column='ar_page_id', null=True)
    ar_parent = IntegerField(db_column='ar_parent_id', null=True)
    ar_rev = IntegerField(db_column='ar_rev_id', null=True)
    ar_sha1 = CharField(max_length=32)
    ar_text = TextField()
    ar_text = IntegerField(db_column='ar_text_id', null=True)
    ar_timestamp = CharField(max_length=14)
    ar_title = CharField(max_length=255)
    ar_user = IntegerField()
    ar_user_text = CharField(max_length=255)

    class Meta:
        db_table = 'archive'

class Category(BaseModel):
    cat_files = IntegerField()
    cat = PrimaryKeyField(db_column='cat_id')
    cat_pages = IntegerField()
    cat_subcats = IntegerField()
    cat_title = CharField(max_length=255)

    class Meta:
        db_table = 'category'

class Categorylinks(BaseModel):
    cl_collation = CharField(max_length=32)
    cl_from = IntegerField(primary_key=True)
    cl_sortkey = CharField(max_length=230)
    cl_sortkey_prefix = CharField(max_length=255)
    cl_timestamp = DateTimeField()
    cl_to = CharField(max_length=255)
    cl_type = CharField(max_length=6)

    class Meta:
        db_table = 'categorylinks'

class ChangeTag(BaseModel):
    ct_log = IntegerField(db_column='ct_log_id', null=True)
    ct_params = TextField(null=True)
    ct_rc = IntegerField(db_column='ct_rc_id', null=True)
    ct_rev = IntegerField(db_column='ct_rev_id', null=True)
    ct_tag = CharField(max_length=255)

    class Meta:
        db_table = 'change_tag'

class Externallinks(BaseModel):
    el_from = IntegerField()
    el = PrimaryKeyField(db_column='el_id')
    el_index = TextField()
    el_to = TextField()

    class Meta:
        db_table = 'externallinks'

class Filearchive(BaseModel):
    fa_archive_name = CharField(max_length=255, null=True)
    fa_bits = IntegerField(null=True)
    fa_deleted = IntegerField()
    fa_deleted_reason = TextField(null=True)
    fa_deleted_timestamp = CharField(max_length=14, null=True)
    fa_deleted_user = IntegerField(null=True)
    fa_description = TextField(null=True)
    fa_height = IntegerField(null=True)
    fa = PrimaryKeyField(db_column='fa_id')
    fa_major_mime = CharField(max_length=11, null=True)
    fa_media_type = CharField(max_length=10, null=True)
    fa_metadata = TextField(null=True)
    fa_minor_mime = CharField(max_length=100, null=True)
    fa_name = CharField(max_length=255)
    fa_sha1 = CharField(max_length=32)
    fa_size = IntegerField(null=True)
    fa_storage_group = CharField(max_length=16, null=True)
    fa_storage_key = CharField(max_length=64, null=True)
    fa_timestamp = CharField(max_length=14, null=True)
    fa_user = IntegerField(null=True)
    fa_user_text = CharField(max_length=255, null=True)
    fa_width = IntegerField(null=True)

    class Meta:
        db_table = 'filearchive'

class Hitcounter(BaseModel):
    hc = IntegerField(db_column='hc_id')

    class Meta:
        db_table = 'hitcounter'

class Image(BaseModel):
    img_bits = IntegerField()
    img_description = TextField()
    img_height = IntegerField()
    img_major_mime = CharField(max_length=11)
    img_media_type = CharField(max_length=10, null=True)
    img_metadata = TextField()
    img_minor_mime = CharField(max_length=100)
    img_name = CharField(max_length=255, primary_key=True)
    img_sha1 = CharField(max_length=32)
    img_size = IntegerField()
    img_timestamp = CharField(max_length=14)
    img_user = IntegerField()
    img_user_text = CharField(max_length=255)
    img_width = IntegerField()

    class Meta:
        db_table = 'image'

class Imagelinks(BaseModel):
    il_from = IntegerField()
    il_to = CharField(max_length=255)

    class Meta:
        db_table = 'imagelinks'

class Interwiki(BaseModel):
    iw_api = TextField()
    iw_local = IntegerField()
    iw_prefix = CharField(max_length=32)
    iw_trans = IntegerField()
    iw_url = TextField()
    iw_wikiid = CharField(max_length=64)

    class Meta:
        db_table = 'interwiki'

class Ipblocks(BaseModel):
    ipb_address = TextField()
    ipb_allow_usertalk = IntegerField()
    ipb_anon_only = IntegerField()
    ipb_auto = IntegerField()
    ipb_block_email = IntegerField()
    ipb_by = IntegerField()
    ipb_by_text = CharField(max_length=255)
    ipb_create_account = IntegerField()
    ipb_deleted = IntegerField()
    ipb_enable_autoblock = IntegerField()
    ipb_expiry = CharField(max_length=14)
    ipb = PrimaryKeyField(db_column='ipb_id')
    ipb_parent_block = IntegerField(db_column='ipb_parent_block_id', null=True)
    ipb_range_end = TextField()
    ipb_range_start = TextField()
    ipb_reason = TextField()
    ipb_timestamp = CharField(max_length=14)
    ipb_user = IntegerField()

    class Meta:
        db_table = 'ipblocks'

class Iwlinks(BaseModel):
    iwl_from = IntegerField()
    iwl_prefix = CharField(max_length=20)
    iwl_title = CharField(max_length=255)

    class Meta:
        db_table = 'iwlinks'

class Job(BaseModel):
    job_attempts = IntegerField()
    job_cmd = CharField(max_length=60)
    job = PrimaryKeyField(db_column='job_id')
    job_namespace = IntegerField()
    job_params = TextField()
    job_random = IntegerField()
    job_sha1 = CharField(max_length=32)
    job_timestamp = CharField(max_length=14, null=True)
    job_title = CharField(max_length=255)
    job_token = CharField(max_length=32)
    job_token_timestamp = CharField(max_length=14, null=True)

    class Meta:
        db_table = 'job'

class L10NCache(BaseModel):
    lc_key = CharField(max_length=255)
    lc_lang = CharField(max_length=32)
    lc_value = TextField()

    class Meta:
        db_table = 'l10n_cache'

class Langlinks(BaseModel):
    ll_from = IntegerField()
    ll_lang = CharField(max_length=20)
    ll_title = CharField(max_length=255)

    class Meta:
        db_table = 'langlinks'

class LogSearch(BaseModel):
    ls_field = CharField(max_length=32)
    ls_log = IntegerField(db_column='ls_log_id')
    ls_value = CharField(max_length=255)

    class Meta:
        db_table = 'log_search'

class Logging(BaseModel):
    log_action = CharField(max_length=32)
    log_comment = CharField(max_length=255)
    log_deleted = IntegerField()
    log = PrimaryKeyField(db_column='log_id')
    log_namespace = IntegerField()
    log_page = IntegerField(null=True)
    log_params = TextField()
    log_timestamp = CharField(max_length=14)
    log_title = CharField(max_length=255)
    log_type = CharField(max_length=32)
    log_user = IntegerField()
    log_user_text = CharField(max_length=255)

    class Meta:
        db_table = 'logging'

class ModuleDeps(BaseModel):
    md_deps = TextField()
    md_module = CharField(max_length=255)
    md_skin = CharField(max_length=32)

    class Meta:
        db_table = 'module_deps'

class MsgResource(BaseModel):
    mr_blob = TextField()
    mr_lang = CharField(max_length=32)
    mr_resource = CharField(max_length=255)
    mr_timestamp = CharField(max_length=14)

    class Meta:
        db_table = 'msg_resource'

class MsgResourceLinks(BaseModel):
    mrl_message = CharField(max_length=255)
    mrl_resource = CharField(max_length=255)

    class Meta:
        db_table = 'msg_resource_links'

class Objectcache(BaseModel):
    exptime = DateTimeField(null=True)
    keyname = CharField(max_length=255, primary_key=True)
    value = TextField(null=True)

    class Meta:
        db_table = 'objectcache'

class Oldimage(BaseModel):
    oi_archive_name = CharField(max_length=255)
    oi_bits = IntegerField()
    oi_deleted = IntegerField()
    oi_description = TextField()
    oi_height = IntegerField()
    oi_major_mime = CharField(max_length=11)
    oi_media_type = CharField(max_length=10, null=True)
    oi_metadata = TextField()
    oi_minor_mime = CharField(max_length=100)
    oi_name = CharField(max_length=255)
    oi_sha1 = CharField(max_length=32)
    oi_size = IntegerField()
    oi_timestamp = CharField(max_length=14)
    oi_user = IntegerField()
    oi_user_text = CharField(max_length=255)
    oi_width = IntegerField()

    class Meta:
        db_table = 'oldimage'

class Page(BaseModel):
    page_content_model = CharField(max_length=32, null=True)
    page_counter = BigIntegerField()
    page = PrimaryKeyField(db_column='page_id')
    page_is_new = IntegerField()
    page_is_redirect = IntegerField()
    page_latest = IntegerField()
    page_len = IntegerField()
    page_links_updated = CharField(max_length=14, null=True)
    page_namespace = IntegerField()
    page_random = FloatField()
    page_restrictions = TextField()
    page_title = CharField(max_length=255)
    page_touched = CharField(max_length=14)

    class Meta:
        db_table = 'page'

class PageProps(BaseModel):
    pp_page = IntegerField()
    pp_propname = CharField(max_length=60)
    pp_sortkey = FloatField(null=True)
    pp_value = TextField()

    class Meta:
        db_table = 'page_props'

class PageRestrictions(BaseModel):
    pr_cascade = IntegerField()
    pr_expiry = CharField(max_length=14, null=True)
    pr = PrimaryKeyField(db_column='pr_id')
    pr_level = CharField(max_length=60)
    pr_page = IntegerField()
    pr_type = CharField(max_length=60)
    pr_user = IntegerField(null=True)

    class Meta:
        db_table = 'page_restrictions'

class Pagelinks(BaseModel):
    pl_from = IntegerField()
    pl_namespace = IntegerField()
    pl_title = CharField(max_length=255)

    class Meta:
        db_table = 'pagelinks'

class ProtectedTitles(BaseModel):
    pt_create_perm = CharField(max_length=60)
    pt_expiry = CharField(max_length=14)
    pt_namespace = IntegerField()
    pt_reason = TextField(null=True)
    pt_timestamp = CharField(max_length=14)
    pt_title = CharField(max_length=255)
    pt_user = IntegerField()

    class Meta:
        db_table = 'protected_titles'

class Querycache(BaseModel):
    qc_namespace = IntegerField()
    qc_title = CharField(max_length=255)
    qc_type = CharField(max_length=32)
    qc_value = IntegerField()

    class Meta:
        db_table = 'querycache'

class QuerycacheInfo(BaseModel):
    qci_timestamp = CharField(max_length=14)
    qci_type = CharField(max_length=32)

    class Meta:
        db_table = 'querycache_info'

class Querycachetwo(BaseModel):
    qcc_namespace = IntegerField()
    qcc_namespacetwo = IntegerField()
    qcc_title = CharField(max_length=255)
    qcc_titletwo = CharField(max_length=255)
    qcc_type = CharField(max_length=32)
    qcc_value = IntegerField()

    class Meta:
        db_table = 'querycachetwo'

class Recentchanges(BaseModel):
    rc_bot = IntegerField()
    rc_comment = CharField(max_length=255)
    rc_cur = IntegerField(db_column='rc_cur_id')
    rc_cur_time = CharField(max_length=14)
    rc_deleted = IntegerField()
    rc = PrimaryKeyField(db_column='rc_id')
    rc_ip = CharField(max_length=40)
    rc_last_oldid = IntegerField()
    rc_log_action = CharField(max_length=255, null=True)
    rc_log_type = CharField(max_length=255, null=True)
    rc_logid = IntegerField()
    rc_minor = IntegerField()
    rc_namespace = IntegerField()
    rc_new = IntegerField()
    rc_new_len = IntegerField(null=True)
    rc_old_len = IntegerField(null=True)
    rc_params = TextField(null=True)
    rc_patrolled = IntegerField()
    rc_source = CharField(max_length=16)
    rc_this_oldid = IntegerField()
    rc_timestamp = CharField(max_length=14)
    rc_title = CharField(max_length=255)
    rc_type = IntegerField()
    rc_user = IntegerField()
    rc_user_text = CharField(max_length=255)

    class Meta:
        db_table = 'recentchanges'

class Redirect(BaseModel):
    rd_fragment = CharField(max_length=255, null=True)
    rd_from = PrimaryKeyField()
    rd_interwiki = CharField(max_length=32, null=True)
    rd_namespace = IntegerField()
    rd_title = CharField(max_length=255)

    class Meta:
        db_table = 'redirect'

class Revision(BaseModel):
    rev_comment = TextField()
    rev_content_format = CharField(max_length=64, null=True)
    rev_content_model = CharField(max_length=32, null=True)
    rev_deleted = IntegerField()
    rev = PrimaryKeyField(db_column='rev_id')
    rev_len = IntegerField(null=True)
    rev_minor_edit = IntegerField()
    rev_page = IntegerField()
    rev_parent = IntegerField(db_column='rev_parent_id', null=True)
    rev_sha1 = CharField(max_length=32)
    rev_text = IntegerField(db_column='rev_text_id')
    rev_timestamp = CharField(max_length=14)
    rev_user = IntegerField()
    rev_user_text = CharField(max_length=255)

    class Meta:
        db_table = 'revision'

class Searchindex(BaseModel):
    si_page = IntegerField()
    si_text = TextField()
    si_title = CharField(max_length=255)

    class Meta:
        db_table = 'searchindex'

class SiteIdentifiers(BaseModel):
    si_key = CharField(max_length=32)
    si_site = IntegerField()
    si_type = CharField(max_length=32)

    class Meta:
        db_table = 'site_identifiers'

class SiteStats(BaseModel):
    ss_active_users = BigIntegerField(null=True)
    ss_good_articles = BigIntegerField(null=True)
    ss_images = IntegerField(null=True)
    ss_row = IntegerField(db_column='ss_row_id')
    ss_total_edits = BigIntegerField(null=True)
    ss_total_pages = BigIntegerField(null=True)
    ss_total_views = BigIntegerField(null=True)
    ss_users = BigIntegerField(null=True)

    class Meta:
        db_table = 'site_stats'

class Sites(BaseModel):
    site_config = TextField()
    site_data = TextField()
    site_domain = CharField(max_length=255)
    site_forward = IntegerField()
    site_global_key = CharField(max_length=32)
    site_group = CharField(max_length=32)
    site = PrimaryKeyField(db_column='site_id')
    site_language = CharField(max_length=32)
    site_protocol = CharField(max_length=32)
    site_source = CharField(max_length=32)
    site_type = CharField(max_length=32)

    class Meta:
        db_table = 'sites'

class TagSummary(BaseModel):
    ts_log = IntegerField(db_column='ts_log_id', null=True)
    ts_rc = IntegerField(db_column='ts_rc_id', null=True)
    ts_rev = IntegerField(db_column='ts_rev_id', null=True)
    ts_tags = TextField()

    class Meta:
        db_table = 'tag_summary'

class Templatelinks(BaseModel):
    tl_from = IntegerField()
    tl_namespace = IntegerField()
    tl_title = CharField(max_length=255)

    class Meta:
        db_table = 'templatelinks'

class Text(BaseModel):
    old_flags = TextField()
    old = PrimaryKeyField(db_column='old_id')
    old_text = TextField()

    class Meta:
        db_table = 'text'

class Transcache(BaseModel):
    tc_contents = TextField(null=True)
    tc_time = CharField(max_length=14)
    tc_url = CharField(max_length=255)

    class Meta:
        db_table = 'transcache'

class Updatelog(BaseModel):
    ul_key = CharField(max_length=255, primary_key=True)
    ul_value = TextField(null=True)

    class Meta:
        db_table = 'updatelog'

class Uploadstash(BaseModel):
    us_chunk_inx = IntegerField(null=True)
    us = PrimaryKeyField(db_column='us_id')
    us_image_bits = IntegerField(null=True)
    us_image_height = IntegerField(null=True)
    us_image_width = IntegerField(null=True)
    us_key = CharField(max_length=255)
    us_media_type = CharField(max_length=10, null=True)
    us_mime = CharField(max_length=255, null=True)
    us_orig_path = CharField(max_length=255)
    us_path = CharField(max_length=255)
    us_props = TextField(null=True)
    us_sha1 = CharField(max_length=31)
    us_size = IntegerField()
    us_source_type = CharField(max_length=50, null=True)
    us_status = CharField(max_length=50)
    us_timestamp = CharField(max_length=14)
    us_user = IntegerField()

    class Meta:
        db_table = 'uploadstash'

class User(BaseModel):
    user_editcount = IntegerField(null=True)
    user_email = TextField()
    user_email_authenticated = CharField(max_length=14, null=True)
    user_email_token = CharField(max_length=32, null=True)
    user_email_token_expires = CharField(max_length=14, null=True)
    user = PrimaryKeyField(db_column='user_id')
    user_name = CharField(max_length=255)
    user_newpass_time = CharField(max_length=14, null=True)
    user_newpassword = TextField()
    user_password = TextField()
    user_password_expires = CharField(max_length=14, null=True)
    user_real_name = CharField(max_length=255)
    user_registration = CharField(max_length=14, null=True)
    user_token = CharField(max_length=32)
    user_touched = CharField(max_length=14)

    class Meta:
        db_table = 'user'

class UserFormerGroups(BaseModel):
    ufg_group = CharField(max_length=255)
    ufg_user = IntegerField()

    class Meta:
        db_table = 'user_former_groups'

class UserGroups(BaseModel):
    ug_group = CharField(max_length=255)
    ug_user = IntegerField()

    class Meta:
        db_table = 'user_groups'

class UserNewtalk(BaseModel):
    user = IntegerField(db_column='user_id')
    user_ip = CharField(max_length=40)
    user_last_timestamp = CharField(max_length=14, null=True)

    class Meta:
        db_table = 'user_newtalk'

class UserProperties(BaseModel):
    up_property = CharField(max_length=255)
    up_user = IntegerField()
    up_value = TextField(null=True)

    class Meta:
        db_table = 'user_properties'

class ValidTag(BaseModel):
    vt_tag = CharField(max_length=255, primary_key=True)

    class Meta:
        db_table = 'valid_tag'

class Watchlist(BaseModel):
    wl_namespace = IntegerField()
    wl_notificationtimestamp = CharField(max_length=14, null=True)
    wl_title = CharField(max_length=255)
    wl_user = IntegerField()

    class Meta:
        db_table = 'watchlist'

