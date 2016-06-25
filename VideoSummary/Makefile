build:
	./activator dist && docker build -t='bmzhao/tldw:latest' .

run:
	./activator dist && docker build -t='bmzhao/tldw:latest' . && docker run --name='tldw' -d -p 80:80 'bmzhao/tldw:latest' && docker logs -f tldw

remove:
	docker stop 'tldw' && docker rm 'tldw'

push:
	./activator dist && docker build -t='bmzhao/tldw:latest' . && docker push 'bmzhao/tldw:latest'
