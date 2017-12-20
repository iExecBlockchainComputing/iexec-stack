-- =============================================================================
--  Copyrights     : CNRS
--  Authors        : Oleg Lodygensky
--  Acknowledgment : XtremWeb-HEP is based on XtremWeb 1.8.0 by INRIA : http://www.xtremweb.net/
--  Web            : http://www.xtremweb-hep.org
-- 
--       This file is part of XtremWeb-HEP.
-- 
--     XtremWeb-HEP is free software: you can redistribute it and/or modify
--     it under the terms of the GNU General Public License as published by
--     the Free Software Foundation, either version 3 of the License, or
--     (at your option) any later version.
--     
--     XtremWeb-HEP is distributed in the hope that it will be useful,
--     but WITHOUT ANY WARRANTY; without even the implied warranty of
--     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
--     GNU General Public License for more details.
-- 
--     You should have received a copy of the GNU General Public License
--     along with XtremWeb-HEP.  If not, see <http://www.gnu.org/licenses/>.
-- =============================================================================


-- =============================================================================
-- 
--  File :  spip3_towordpress41.sql
-- 
--  Requirements   : your WordPress4.1 database name; your Spip3 database dump
--                   wordpress database must already exist
--                   spip      database must already exist
--  Purpose        : this script convert Spip3 database to WordPress4.1
--                   - converts Spip3 articles to WordPress4.1 posts
-- 
--     
--     This is free software: you can redistribute it and/or modify
--     it under the terms of the GNU General Public License as published by
--     the Free Software Foundation, either version 3 of the License, or
--     (at your option) any later version.
--     
-- 
--  2015-02-12  Oleg Lodygensky (lodygens A_T lal D_O_T IN2P3 D_O_T fr)
-- 
-- =============================================================================


-- Source http://contrib.spip.net/Export-Spip-vers-Wordpress

-- 
-- Oleg : this is to migrate from Spip 3 to WordPress 4.1
-- 
-- (1) my site was on spip 3 (3.0.1 released on march 2013, I think)
-- (2) my database name was "SPIPDATABASENAME"
--     please replace by your own database name

 
-- Imports terms
 
REPLACE INTO wp_terms(term_id, name, slug, term_group)
SELECT id_rubrique, titre, CONCAT("rub",id_rubrique), 1 FROM SPIPDATABASENAME.spip_rubriques;

-- Update urls
 
UPDATE wp_terms, SPIPDATABASENAME.spip_urls
SET slug = SPIPDATABASENAME.spip_urls.url
WHERE SPIPDATABASENAME.spip_urls.id_objet = term_id
AND SPIPDATABASENAME.spip_urls.type = "rubrique";
 
 
-- Import posts
-- Oleg : I corrected an error from the original 
-- (*) Spip 3 uses articles versionning 
-- 
REPLACE INTO wp_posts(
ID
, post_author
, post_date, post_date_gmt
, post_content
, post_title
#, post_category
# , post_status
, to_ping , pinged
, post_modified, post_modified_gmt
 
)
 
SELECT
p.id_article
, v.id_auteur
, p.date, p.date
, concat(p.chapo, p.descriptif, p.texte)
, titre
#, p.id_rubrique
, '', ''
, p.date_modif, p.date_modif
FROM
SPIPDATABASENAME.spip_articles AS p
LEFT JOIN SPIPDATABASENAME.spip_versions AS v ON p.id_version = v.id_version;
 
-- Link posts to terms
 
REPLACE INTO wp_term_relationships(object_id, term_taxonomy_id)
SELECT p.id_article, p.id_rubrique FROM SPIPDATABASENAME.spip_articles AS p;

-- Oleg : 
-- Following is expected to insert expected row into tables
-- (1) wp_term_taxonomy
-- (2) wp_term_relationships
 
REPLACE INTO wp_term_taxonomy(term_id, parent, taxonomy)
SELECT id_rubrique, id_parent,"category" FROM SPIPDATABASENAME.spip_rubriques;

update wp_term_taxonomy as t set count=
(select count(*) 
 from wp_posts as p ,wp_term_relationships as r 
 where p.id=r.object_id 
   and r.term_taxonomy_id=t.term_taxonomy_id
);
 


-- 
-- Oleg : list resulted rows
-- 
select p.post_title, te.name as categorie 
from wp_posts as p , wp_terms as te , wp_term_taxonomy as ta , wp_term_relationships as r 
where p.id=r.object_id 
  and r.term_taxonomy_id=ta.term_taxonomy_id 
  and ta.term_id = te.term_id ;



-- Oleg : don't import comments
-- Import comments
 
-- REPLACE INTO wp_comments(
-- comment_ID
-- , comment_post_ID
-- , comment_author
-- , comment_author_email
-- ,comment_author_url
-- , comment_date
-- , comment_date_gmt
-- , comment_content
-- , comment_parent
-- , comment_approved
-- )
 
-- SELECT
-- id_forum
-- , id_article
-- , auteur
-- , email_auteur
-- , url_site
-- , date_heure
-- , date_heure
-- , texte
-- , id_parent
-- , 0
-- FROM SPIPDATABASENAME.spip_forum;

-- Oleg : don't approve comments 
-- Approve comments ( not really working here )
 
-- update wp_comments, SPIPDATABASENAME.spip_forum
-- SET comment_approved = 1
-- WHERE wp_comments .comment_ID = SPIPDATABASENAME.spip_forum.id_article
-- AND SPIPDATABASENAME.spip_forum.statut = "publie";
 
-- Oleg : don't update comments
-- Update comments numbers per post ( Nor Working for me )
 
-- UPDATE wp_posts
-- SET comment_count = (SELECT COUNT( * )
-- from wp_comments, wp_posts WHERE comment_post_ID = ID and comment_approved = 1);

 
-- Update the syntax (basically transform weird SPIP stuff into HTML
 
 
update wp_posts set post_content = replace(post_content, '{{', ' <b> ') where instr(post_content, '{{') > 0;
update wp_posts set post_content = replace(post_content, '}}', ' </b> ') where instr(post_content, '}}') > 0;
update wp_posts set post_content = replace(post_content, '{', ' <i> ') where instr(post_content, '{') > 0;
update wp_posts set post_content = replace(post_content, '}', ' </i> ') where instr(post_content, '}') > 0;
update wp_posts set post_content = replace(post_content, '{{{', ' <h1> ') where instr(post_content, '{{{') > 0;
update wp_posts set post_content = replace(post_content, '}}}', ' </h1> ') where instr(post_content, '}}}') > 0;
update wp_posts set post_content = replace(post_content, '[[', ' <blockquote> ') where instr(post_content, '[[') > 0;
update wp_posts set post_content = replace(post_content, ']]', ' </blockquote> ') where instr(post_content, ']]') > 0;
update wp_posts set post_content = replace(post_content, '*]', ' </strong></i> ') where instr(post_content, '*]') > 0;
update wp_posts set post_content = replace(post_content, '[*', ' </strong></i> ') where instr(post_content, '*]') > 0;
